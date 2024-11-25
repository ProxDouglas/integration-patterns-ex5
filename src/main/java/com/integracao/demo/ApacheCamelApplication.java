package com.integracao.demo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;


@SpringBootApplication
public class ApacheCamelApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApacheCamelApplication.class, args);

        Thread.sleep(5000);
    }

    @Bean
    public RouteBuilder jsonToXmlRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() {

                // Lê o arquivo e publica em múltiplos destinos usando multicast
                from("file:resource/inbox?fileName=pedido.json&noop=true")
                        .unmarshal().json(JsonLibrary.Jackson, Map.class)
                        .multicast()
                        .to("direct:estoque", "direct:notificacao", "direct:relatorio");


                from("direct:estoque")
                        .process(exchange -> {
                            var bory1 = exchange.getMessage().getBody(Map.class);
                            Map<String, Object> body = exchange.getMessage().getBody(Map.class);
                            body.remove("pagamento");
                            body.remove("cliente");
                            body.remove("endereco");
                            exchange.getMessage().setBody(body);
                        })
                        .marshal().jacksonXml()
                        .to("file:resource/estoque?fileName=pedido_estoque.xml")
                        .log("Arquivo para estoque criado com sucesso.");


                from("direct:notificacao")
                        .process(exchange -> {
                            Map<String, Object> body = exchange.getMessage().getBody(Map.class);
                            body.remove("pagamento");
                            exchange.getMessage().setBody(body);
                        })
                        .marshal().jacksonXml()
                        .to("file:resource/notification?fileName=pedido_notification.xml")
                        .log("Arquivo para notificacao criado com sucesso.");


                from("direct:relatorio")
                        .process(exchange -> {
                            Map<String, Object> body = exchange.getMessage().getBody(Map.class);
                            body.remove("pagamento");
                            exchange.getMessage().setBody(body);
                        })
                        .marshal().jacksonXml()
                        .to("file:resource/relatorio?fileName=pedido_relatorio.xml")
                        .log("Arquivo para relatorio criado com sucesso.");
            }
        };
    }

}
