package com.gymcrm.configuration.messaging.health;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsHealthIndicator implements HealthIndicator {
    private final AmazonSQSAsync amazonSQSAsync;
    
    @Value("${aws.sqs.trainer-workload-queue}")
    private String trainerWorkloadQueue;
    
    @Override
    public Health health() {
        try {
            // Try to get queue attributes to test connection
            GetQueueAttributesRequest request = new GetQueueAttributesRequest()
                    .withQueueUrl(getQueueUrl(trainerWorkloadQueue))
                    .withAttributeNames("All");
            
            amazonSQSAsync.getQueueAttributes(request);
            
            return Health.up()
                    .withDetail("status", "SQS connection is established")
                    .withDetail("queue", trainerWorkloadQueue)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("queue", trainerWorkloadQueue)
                    .build();
        }
    }
    
    private String getQueueUrl(String queueName) {
        // If it's already a full URL, return as is
        if (queueName.startsWith("https://")) {
            return queueName;
        }
        // Otherwise, get the queue URL by name
        return amazonSQSAsync.getQueueUrl(queueName).getQueueUrl();
    }
}
