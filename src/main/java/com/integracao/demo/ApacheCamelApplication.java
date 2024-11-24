package com.integracao.demo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ApacheCamelApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApacheCamelApplication.class, args);

//		context.start();
		Thread.sleep(5000);
//		context.stop();
    }

    @Bean
    public RouteBuilder jsonToXmlRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() {

                from("file:resource/inbox?fileName=pedido.json&noop=true")
                        .unmarshal().json(JsonLibrary.Jackson)
                        .marshal().jacksonXml()
                        .to("file:resource/outbox?fileName=pedido2.xml")
                        .log("Arquivo XML gerado com sucesso em: ${file:absolute.path}");
            }
        };
    }

}
