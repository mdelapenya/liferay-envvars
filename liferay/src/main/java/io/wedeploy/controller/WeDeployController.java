package io.wedeploy.controller;

import com.liferay.portal.configuration.EnvVariableDecoder;
import com.liferay.portal.configuration.EnvVariableEncoder;

import com.wedeploy.api.ApiClient;

import java.util.Map;

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

    public WeDeployController() {
		Map<String, String> env = System.getenv();

		while (dbUrl == null) {
			if (env.containsKey("WEDEPLOY_DB_URL")) {
				dbUrl = env.get("WEDEPLOY_DB_URL");

				hitsManager = new HitsManager(dbUrl);
			}
			else {
				System.out.println(
					"Please set up an environment variable with the URL to " +
						"the datastore service, with key WEDEPLOY_DB_URL");

				try {
					Thread.sleep(5000);
				}
				catch (InterruptedException e) {
					throw new RuntimeException(
						"Cannot interrupt process while waiting for proper " +
							"environment configuration");
				}
			}
		}
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

    @RequestMapping(value = "/decode/{key}", method = RequestMethod.GET)
    public String decode(Model model, @PathVariable("key") String key) {
		if (key == null || key.isEmpty() ||
			!key.startsWith(Constants.ENV_OVERRIDE_PREFIX)) {

			model.addAttribute(
				"error", "Liferay Env Vars starts with LIFERAY_ prefix");
		}
		else {
			String envKey = key.substring(
				Constants.ENV_OVERRIDE_PREFIX.length());

			String decodedKey = EnvVariableDecoder.decode(envKey.toLowerCase());

			model.addAttribute("liferayKey", key);
			model.addAttribute("decodedKey", decodedKey);

			Integer currentHits = hitsManager.incrementHits(
				Constants.DECODES_PATH, key);

			model.addAttribute("hits", currentHits);
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

			Integer currentHits = hitsManager.incrementHits(Constants.ENCODES_PATH, key);

			model.addAttribute("hits", currentHits);
		}

		return "encode";
	}


	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("layout");
	}

	private String dbUrl;
    private HitsManager hitsManager;

	static {
		ApiClient.init();
	}

}