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
 * This presents an opportunity for the developer to use either application.properties to provide connection info to his
 * CloudFoundry's Cloud Controller API, or to use a CloudFoundry User Provided Service named by the property
 * ecs.cc.serviceId (or default "cc").  
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
		source.put("ecs.cc.trustSelfSigned", "${vcap.services.${ecs.cc.serviceId:cc}.credentials.trustSelfSigned}");

		event.getEnvironment().getPropertySources().addLast(new MapPropertySource("ecsCloudLaunchProperties", source));
	}
}
