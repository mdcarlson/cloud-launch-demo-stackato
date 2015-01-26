/*
 * Copyright 2015 ECS Team, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecsteam.cloudlaunch.services.jenkins;

import java.net.URI;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecsteam.cloudlaunch.services.jenkins.model.JobMonitorResponseFragment;
import com.ecsteam.cloudlaunch.services.jenkins.model.MonitorResponse;
import com.ecsteam.cloudlaunch.services.jenkins.model.QueueItemResponseFragment;
import com.ecsteam.cloudlaunch.services.jenkins.model.QueuedBuildResponse;

/**
 * @author Josh Ghiloni
 *
 */
@Service
public class JenkinsService {

	@Value("${ecs.jenkins.baseUrl:}")
	private String baseUrl;

	@Value("${ecs.jenkins.jobName:}")
	private String jobName;

	@Value("${ecs.jenkins.user:}")
	private String user;

	@Value("${ecs.jenkins.password:}")
	private String password;

	private HttpEntity<String> authEntity;

	public QueuedBuildResponse triggerBuild() {
		String urlTemplate = "{baseUrl}/job/{jobName}/build";

		RestTemplate template = new RestTemplate();
		ResponseEntity<Object> response = template.exchange(urlTemplate, HttpMethod.POST, getAuthorizationEntity(),
				Object.class, baseUrl, jobName);

		if (HttpStatus.CREATED.equals(response.getStatusCode())) {
			HttpHeaders headers = response.getHeaders();
			URI queueUri = headers.getLocation();

			String last = null;
			String current = null;
			String next = null;

			String[] parts = queueUri.getPath().split("/");

			QueuedBuildResponse responseObject = new QueuedBuildResponse();
			for (int i = parts.length - 1; i >= 0; --i) {
				last = parts[i];
				current = parts[i - 1];
				next = parts[i - 2];

				if ("queue".equals(next) && "item".equals(current)) {
					responseObject = new QueuedBuildResponse();
					responseObject.setMonitorUri(String.format("/services/build/queue/%s", last));

					return responseObject;
				}
			}
		}
		return null;
	}

	public QueuedBuildResponse getJobNumberFromQueue(String queueId) {
		RestTemplate template = new RestTemplate();
		ResponseEntity<QueueItemResponseFragment> fragmentEntity = template.exchange(
				"{baseUrl}/queue/item/{queueId}/api/json", HttpMethod.GET, getAuthorizationEntity(),
				QueueItemResponseFragment.class, baseUrl, queueId);

		QueueItemResponseFragment fragment = fragmentEntity.getBody();
		if (fragment.getExecutable() == null || fragment.getExecutable().getUrl() == null) {
			QueuedBuildResponse response = new QueuedBuildResponse();
			response.setWait(1000L);
			return response;
		} else {
			String monitorUrl = fragment.getExecutable().getUrl();
			String last = null;
			String current = null;
			String next = null;

			String[] parts = monitorUrl.split("/");

			QueuedBuildResponse responseObject = new QueuedBuildResponse();
			for (int i = parts.length - 1; i >= 0; --i) {
				last = parts[i];
				current = parts[i - 1];
				next = parts[i - 2];

				if ("job".equals(next) && jobName.equals(current)) {
					responseObject = new QueuedBuildResponse();
					responseObject.setMonitorUri(String.format("/services/build/job/%s", last));

					return responseObject;
				}
			}
		}
		
		return null;
	}

	public MonitorResponse monitorJob(String jobNumber) {
		String urlTemplate = "{baseUrl}/job/{jobName}/{jobNumber}/api/json";

		RestTemplate template = new RestTemplate();

		ResponseEntity<JobMonitorResponseFragment> fragmentEntity = template.exchange(urlTemplate, HttpMethod.GET,
				getAuthorizationEntity(), JobMonitorResponseFragment.class, baseUrl, jobName, jobNumber);

		if (HttpStatus.OK.equals(fragmentEntity.getStatusCode())) {
			JobMonitorResponseFragment fragment = fragmentEntity.getBody();

			MonitorResponse response = new MonitorResponse();
			response.setDuration(fragment.getDuration());
			response.setStillBuilding(fragment.isBuilding());
			response.setResult(fragment.getResult());

			return response;
		}

		return null;
	}

	private HttpEntity<String> getAuthorizationEntity() {
		if (authEntity == null) {
			String unencoded = user + ":" + password;
			String encoded = Base64.getEncoder().encodeToString(unencoded.getBytes());

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Basic " + encoded);

			authEntity = new HttpEntity<String>(headers);
		}

		return authEntity;
	}
}
