package com.devcamp.bery_real_estate.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Subscription;
import com.devcamp.bery_real_estate.services.ISubscriptionService;



@RestController
@CrossOrigin
@RequestMapping("/")
public class SubscriptionController {
    @Autowired
    private ISubscriptionService subscriptionService;

    /**
     * get all
     * @return
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        try {
            List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
            return new ResponseEntity<>(subscriptions, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * get by id
     * @param id
     * @return
     */
    @GetMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<Object> getSubscriptionById(@PathVariable(name = "subscriptionId", required = true) Integer id) {
        try {
            Subscription subscription = subscriptionService.getSubscriptionById(id);
            return new ResponseEntity<>(subscription, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * add
     * @param pSubscription
     * @return
     */
    @PostMapping("/subscriptions")
    public ResponseEntity<Object> createSubscription(@Valid @RequestBody Subscription pSubscription) {
        try {
            Subscription subscription = subscriptionService.createSubscription(pSubscription);
            return new ResponseEntity<>(subscription, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param id
     * @param pSubscription
     * @return
     */
    @PutMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<Object> updateSubscription(@PathVariable(name = "subscriptionId") Integer id,
            @Valid @RequestBody Subscription pSubscription) {
        try {
            Subscription subscription = subscriptionService.updateSubscription(id, pSubscription);
            return new ResponseEntity<>(subscription, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete
     * @param id
     * @return
     */
    @DeleteMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<Object> deleteSubscription(@PathVariable(name = "subscriptionId") Integer id) {
        try {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
