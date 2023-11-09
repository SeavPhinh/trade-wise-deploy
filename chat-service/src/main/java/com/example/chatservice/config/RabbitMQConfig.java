//package com.example.chatservice.config;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMQConfig {
//
//    public static final String DIRECT_QUEUE = "directQueue";
//
//    @Bean
//    public Queue directQueue() {
//        return new Queue(DIRECT_QUEUE);
//    }
//
////    @Bean
////    public MessageConverter jsonMessageConverter() {
////        return new Jackson2JsonMessageConverter();
////    }
////
////    @Bean
////    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
////        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
////        rabbitTemplate.setMessageConverter(jsonMessageConverter());
////        return rabbitTemplate;
////    }
//

//}
