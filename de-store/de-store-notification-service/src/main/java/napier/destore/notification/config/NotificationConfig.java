package napier.destore.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import napier.destore.common.event.EventTopics;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public TopicExchange destoreExchange() {
        return new TopicExchange(EventTopics.DESTORE_EXCHANGE);
    }

    // Queue for low stock events
    @Bean
    public Queue lowStockQueue() {
        return new Queue("destore.notifications.lowstock", true);
    }

    @Bean
    public Binding lowStockBinding(@Qualifier("lowStockQueue") Queue lowStockQueue, 
                                    TopicExchange destoreExchange) {
        return BindingBuilder.bind(lowStockQueue)
                .to(destoreExchange)
                .with(EventTopics.INVENTORY_LOW_STOCK);
    }

    @Bean
    public Binding outOfStockBinding(@Qualifier("lowStockQueue") Queue lowStockQueue, 
                                      TopicExchange destoreExchange) {
        return BindingBuilder.bind(lowStockQueue)
                .to(destoreExchange)
                .with(EventTopics.INVENTORY_OUT_OF_STOCK);
    }

    // Queue for finance decision events
    @Bean
    public Queue financeDecisionQueue() {
        return new Queue("destore.notifications.finance", true);
    }

    @Bean
    public Binding financeDecisionBinding(@Qualifier("financeDecisionQueue") Queue financeDecisionQueue, 
                                           TopicExchange destoreExchange) {
        return BindingBuilder.bind(financeDecisionQueue)
                .to(destoreExchange)
                .with(EventTopics.FINANCE_DECISION);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }
}