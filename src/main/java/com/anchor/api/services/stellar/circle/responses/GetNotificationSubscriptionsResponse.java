package com.anchor.api.services.stellar.circle.responses;

import com.anchor.api.services.stellar.circle.models.NotificationSubscription;

import java.util.ArrayList;
import java.util.List;

public class GetNotificationSubscriptionsResponse {
    List<NotificationSubscription> data = new ArrayList<>();

    public List<NotificationSubscription> getData() {
        return data;
    }

    public void setData(List<NotificationSubscription> data) {
        this.data = data;
    }
}
