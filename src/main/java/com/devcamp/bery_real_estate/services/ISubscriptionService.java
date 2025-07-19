package com.devcamp.bery_real_estate.services;

import java.util.List;

import com.devcamp.bery_real_estate.models.Subscription;

public interface ISubscriptionService {
    /**
     * get all
     * @return
     */
    List<Subscription> getAllSubscriptions();

    /**
     * get by id
     * @param id
     * @return
     */
    Subscription getSubscriptionById(Integer id);

    /**
     * add
     * @param pSubscription
     * @return
    */
    Subscription createSubscription(Subscription pSubscription);

    /**
     * update
     * @param id
     * @param pSubscription
     * @return
    */
    Subscription updateSubscription(Integer id, Subscription pSubscription);

    /**
     * delete
     * @param id
    */
    void deleteSubscription(Integer id);
}
