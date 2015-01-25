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

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * The use of the cf-java-client introduces spring-security onto the classpath, which by default enables global web
 * security. We don't want to have any of our endpoints secured, so disable it everywhere.
 * 
 * @author Josh Ghiloni
 *
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().anyRequest();
	}
}
