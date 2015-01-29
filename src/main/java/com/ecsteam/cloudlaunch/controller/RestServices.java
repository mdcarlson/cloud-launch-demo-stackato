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
package com.ecsteam.cloudlaunch.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecsteam.cloudlaunch.services.github.GithubService;
import com.ecsteam.cloudlaunch.services.github.model.GithubCommit;
import com.ecsteam.cloudlaunch.services.jenkins.JenkinsService;
import com.ecsteam.cloudlaunch.services.jenkins.model.MonitorResponse;
import com.ecsteam.cloudlaunch.services.jenkins.model.QueuedBuildResponse;
import com.ecsteam.cloudlaunch.services.statistics.ApplicationStatisticsProvider;
import com.ecsteam.cloudlaunch.services.statistics.model.ApplicationStatistics;

/**
 * Backend JSON Services
 * 
 * @author Josh Ghiloni
 *
 */
@RestController
@RequestMapping("/services")
public class RestServices {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationStatisticsProvider statisticsProvider;

	@Autowired
	private JenkinsService jenkinsService;

	@Autowired
	private GithubService githubService;

	@Value("${ecs.deployed.sha:}")
	private String latestSha;

	@RequestMapping("/statistics")
	public ApplicationStatistics statistics(HttpServletRequest request) throws IOException {
		ApplicationStatistics statistics = statisticsProvider.getCurrentStatistics();

		statistics.setHost(request.getLocalAddr());
		statistics.setPort(request.getLocalPort());

		return statistics;
	}

	@RequestMapping("/kill")
	public String killInstance() {
		System.exit(-1);
		return "Killed";
	}

	@RequestMapping(value = "/builds/trigger", method = RequestMethod.POST)
	public ResponseEntity<QueuedBuildResponse> triggerBuild() {
		QueuedBuildResponse responseBody = null;
		ResponseEntity<QueuedBuildResponse> response = null;

		responseBody = jenkinsService.triggerBuild();
		if (responseBody != null) {
			response = new ResponseEntity<QueuedBuildResponse>(responseBody, HttpStatus.OK);
		}
		else {
			response = new ResponseEntity<QueuedBuildResponse>(HttpStatus.BAD_REQUEST);
		}

		return response;
	}

	@RequestMapping(value = "/builds/queue/{queueId}", method = RequestMethod.GET)
	public ResponseEntity<QueuedBuildResponse> getJobNumberFromQueue(@PathVariable("queueId") String queueId) {
		QueuedBuildResponse responseBody = null;
		ResponseEntity<QueuedBuildResponse> response = null;

		responseBody = jenkinsService.getJobNumberFromQueue(queueId);
		if (responseBody != null) {
			response = new ResponseEntity<QueuedBuildResponse>(responseBody, HttpStatus.OK);
		}
		else {
			response = new ResponseEntity<QueuedBuildResponse>(HttpStatus.BAD_REQUEST);
		}

		return response;
	}

	@RequestMapping(value = "/builds/job/{jobNumber}", method = RequestMethod.GET)
	public ResponseEntity<MonitorResponse> monitorBuild(@PathVariable("jobNumber") String jobNumber) {
		MonitorResponse responseBody = null;
		ResponseEntity<MonitorResponse> response = null;

		responseBody = jenkinsService.monitorJob(jobNumber);
		if (responseBody != null) {
			response = new ResponseEntity<MonitorResponse>(responseBody, HttpStatus.OK);
		}
		else {
			response = new ResponseEntity<MonitorResponse>(HttpStatus.BAD_REQUEST);
		}

		return response;
	}

	@RequestMapping(value = "/buildInfo/source", method = RequestMethod.GET)
	public ResponseEntity<GithubCommit> getLatestCommitInfo() {
		GithubCommit responseBody = githubService.getLatestCommit();

		if (responseBody != null) {
			return new ResponseEntity<GithubCommit>(responseBody, HttpStatus.OK);
		}
		else {
			return new ResponseEntity<GithubCommit>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/buildInfo/deployed", method = RequestMethod.GET)
	public Map<String, String> getDeployedSha() {
		// show null if empty string
		String sha = null;
		if (StringUtils.hasText(latestSha)) {
			sha = latestSha;
		}
		
		return Collections.singletonMap("deployedSha", sha);
	}

	@RequestMapping(value = "/pagetext", method = RequestMethod.GET)
	public Map<String, List<String>> getPageText() throws IOException {
		Resource resource = applicationContext.getResource("classpath:/pagetext.txt");

		InputStream in = resource.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		List<String> lines = new ArrayList<String>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		return Collections.singletonMap("pageText", lines);
	}
}
