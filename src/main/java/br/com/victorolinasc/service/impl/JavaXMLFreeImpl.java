package br.com.victorolinasc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.victorolinasc.service.api.JavaXMLFree;

@Service
public class JavaXMLFreeImpl implements JavaXMLFree {

	private static final Logger LOG = LoggerFactory
			.getLogger(JavaXMLFreeImpl.class);

	@Override
	public String test() {
		LOG.info("Calling test");
		return "<html><body>Called test() </body></html>";
	}
}