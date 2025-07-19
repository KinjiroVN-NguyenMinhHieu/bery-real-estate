package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Subscription;
import com.devcamp.bery_real_estate.repositories.ISubscriptionRepository;
import com.devcamp.bery_real_estate.services.ISubscriptionService;

@Service
public class SubscriptionServiceImpl implements ISubscriptionService {
    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription getSubscriptionById(Integer id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The subscription is not found"));
    }

    @Override
    public Subscription createSubscription(Subscription pSubscription) {
        Subscription subscription = new Subscription();
        subscription.setUser(pSubscription.getUser());
        subscription.setEndpoint(pSubscription.getEndpoint());
        subscription.setPublickey(pSubscription.getPublickey());
        subscription.setAuthenticationtoken(pSubscription.getAuthenticationtoken());
        subscription.setContentencoding(pSubscription.getContentencoding());
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription updateSubscription(Integer id, Subscription pSubscription) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The subscription is not found"));

        subscription.setUser(pSubscription.getUser());
        subscription.setEndpoint(pSubscription.getEndpoint());
        subscription.setPublickey(pSubscription.getPublickey());
        subscription.setAuthenticationtoken(pSubscription.getAuthenticationtoken());
        subscription.setContentencoding(pSubscription.getContentencoding());
        return subscriptionRepository.save(subscription);
    }

    @Override
    public void deleteSubscription(Integer id) {
        subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The subscription is not found"));
        subscriptionRepository.deleteById(id);
    }
}
