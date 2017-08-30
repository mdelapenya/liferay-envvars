package io.wedeploy.controller;

import com.liferay.portal.configuration.EnvVariableDecoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class WeDeployController {

    public static final String ENV_OVERRIDE_PREFIX = "LIFERAY_";

    public WeDeployController() {

    }

    public static void main(String[] args) {
        SpringApplication.run(WeDeployController.class, args);
    }

    @RequestMapping("/")
    public ModelAndView hello() {
        return new ModelAndView("layout");
    }

    @RequestMapping(value = "/decode/{key}", method = RequestMethod.GET)
    @ResponseBody
    public String decode(@PathVariable("key") String key) {
        String envKey = key.substring(
            WeDeployController.ENV_OVERRIDE_PREFIX.length());

        String decodedKey = EnvVariableDecoder.decode(envKey.toLowerCase());

        return decodedKey;
    }

}