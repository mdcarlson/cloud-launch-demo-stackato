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
package com.ecsteam.cloudlaunch.config;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import com.ecsteam.cloudlaunch.statistics.ApplicationStatisticsProvider;
import com.ecsteam.cloudlaunch.statistics.CloudFoundryStatisticsProvider;
import com.ecsteam.cloudlaunch.statistics.JvmStatisticsProvider;

/**
 * Create an instance of {@link ApplicationStatisticsProvider} that will provide statistics depending on which
 * environment the application is running in.
 * 
 * @author Josh Ghiloni
 *
 */
@Configuration
public class StatisticsConfiguration {

	/**
	 * Create an instance of the provider that can call the Cloud Foundry CloudController APIs to get statistic info
	 * 
	 * @return
	 */
	@Conditional(InCloudFoundry.class)
	@Bean
	public ApplicationStatisticsProvider cloudFoundryStatisticsProvider() {
		return new CloudFoundryStatisticsProvider();
	}

	/**
	 * Create an instance of the provider that only uses info available from the JVM
	 * 
	 * @return
	 */
	@Conditional(NotInCloudFoundry.class)
	@Bean
	public ApplicationStatisticsProvider jvmStatisticsProvider() {
		return new JvmStatisticsProvider();
	}

	/**
	 * Matches if the VCAP_APPLICATION environment variable is present, letting us know that we are in -- or are trying
	 * to pretend we are in -- a Cloud Foundry environment
	 * 
	 * @author Josh Ghiloni
	 *
	 */
	private static class InCloudFoundry extends SpringBootCondition {
		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			String vcapApp = System.getenv("VCAP_APPLICATION");

			if (StringUtils.hasText(vcapApp)) {
				return ConditionOutcome.match("VCAP_APPLICATION env variable is present");
			}
			else {
				return ConditionOutcome.noMatch("VCAP_APPLICATION env variable is not present");
			}
		}
	}

	/**
	 * Matches if {@link InCloudFoundry} does NOT match
	 * 
	 * @author Josh Ghiloni
	 *
	 */
	private static class NotInCloudFoundry extends SpringBootCondition {
		private InCloudFoundry opposite = new InCloudFoundry();

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConditionOutcome negativeOutcome = opposite.getMatchOutcome(context, metadata);

			if (negativeOutcome.isMatch()) {
				return ConditionOutcome.noMatch(negativeOutcome.getMessage());
			}
			else {
				return ConditionOutcome.match(negativeOutcome.getMessage());
			}
		}
	}
}
