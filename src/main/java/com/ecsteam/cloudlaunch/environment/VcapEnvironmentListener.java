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
package com.ecsteam.cloudlaunch.environment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.MapPropertySource;

/**
 * This presents an opportunity for the developer to use either application.properties to provide connection info for
 * various services, or an equivalent cloud foundry User Provided Service
 * 
 * @author Josh Ghiloni
 *
 */
public class VcapEnvironmentListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {
	private int order = ConfigFileApplicationListener.DEFAULT_ORDER + 10;

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		Map<String, Object> properties = new RelaxedPropertyResolver(event.getEnvironment())
				.getSubProperties("vcap.services.");

		if (properties == null || properties.isEmpty()) {
			return;
		}

		Map<String, Object> source = new HashMap<String, Object>();

		source.put("ecs.cc.user", "${vcap.services.${ecs.cc.serviceId:cc}.credentials.user}");
		source.put("ecs.cc.password", "${vcap.services.${ecs.cc.serviceId:cc}.credentials.password}");
		source.put("ecs.cc.url", "${vcap.services.${ecs.cc.serviceId:cc}.credentials.url}");
		source.put("ecs.cc.trustSelfSigned", "${vcap.services.${ecs.cc.serviceId:cc}.credentials.trustSelfSigned:false}");

		source.put("ecs.jenkins.baseUrl", "${vcap.services.${ecs.jenkins.serviceId:jenkins}.credentials.baseUrl}");
		source.put("ecs.jenkins.jobName", "${vcap.services.${ecs.jenkins.serviceId:jenkins}.credentials.jobName}");
		source.put("ecs.jenkins.user", "${vcap.services.${ecs.jenkins.serviceId:jenkins}.credentials.user}");
		source.put("ecs.jenkins.password", "${vcap.services.${ecs.jenkins.serviceId:jenkins}.credentials.password}");
		
		source.put("ecs.github.clientId",  "${vcap.services.${ecs.github.serviceId:github}.credentials.clientId}");
		source.put("ecs.github.clientSecret", "${vcap.services.${ecs.github.serviceId:github}.credentials.clientSecret}");
		source.put("ecs.github.repoName", "${vcap.services.${ecs.github.serviceId:github}.credentials.repoName}");
		source.put("ecs.github.repoOwner", "${vcap.services.${ecs.github.serviceId:github}.credentials.repoOwner}");
		source.put("ecs.github.accessToken", "${vcap.services.${ecs.github.serviceId:github}.credentials.accessToken}");

		event.getEnvironment().getPropertySources().addLast(new MapPropertySource("ecsCloudLaunchProperties", source));
	}
}
