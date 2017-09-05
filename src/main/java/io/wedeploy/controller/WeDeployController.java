package io.wedeploy.controller;

import com.liferay.portal.configuration.EnvVariableDecoder;
import com.liferay.portal.configuration.EnvVariableEncoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class WeDeployController extends WebMvcConfigurerAdapter {

    public static final String ENV_OVERRIDE_PREFIX = "LIFERAY_";

    public WeDeployController() {

    }

    public static void main(String[] args) {
        SpringApplication.run(WeDeployController.class, args);
    }

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);
	}

	@Override
	public void configureContentNegotiation(
		ContentNegotiationConfigurer configurer) {

		configurer.favorPathExtension(false);
	}

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("layout");
    }

    @RequestMapping(value = "/decode/{key}", method = RequestMethod.GET)
    public String decode(Model model, @PathVariable("key") String key) {
		if (key == null || key.isEmpty() ||
			!key.startsWith(ENV_OVERRIDE_PREFIX)) {

			model.addAttribute(
				"error", "Liferay Env Vars start with LIFERAY_ prefix");
		}
		else {
			String envKey = key.substring(
				WeDeployController.ENV_OVERRIDE_PREFIX.length());

			String decodedKey = EnvVariableDecoder.decode(envKey.toLowerCase());

			model.addAttribute("liferayKey", key);
			model.addAttribute("decodedKey", decodedKey);
		}

        return "decode";
    }

	@RequestMapping(value = "/encode/{key}", method = RequestMethod.GET)
	public String encode(Model model, @PathVariable("key") String key) {
		if (key == null || key.isEmpty()) {

			model.addAttribute("error", "Please add a Liferay Portal property");
		}
		else {
			String encodedKey = EnvVariableEncoder.encode(key);

			model.addAttribute("liferayKey", key);
			model.addAttribute("encodedKey", encodedKey);
		}

		return "encode";
	}

}