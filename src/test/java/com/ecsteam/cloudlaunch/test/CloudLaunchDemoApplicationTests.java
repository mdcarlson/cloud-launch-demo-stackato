package com.ecsteam.cloudlaunch.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ecsteam.cloudlaunch.CloudLaunchDemoApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CloudLaunchDemoApplication.class)
@WebAppConfiguration
public class CloudLaunchDemoApplicationTests {

	@Test
	public void contextLoads() {
	}

}
