package napier.destore.common.event;

public final class EventTopics {

    private EventTopics() {
    }

    // Exchange names
    public static final String DESTORE_EXCHANGE = "destore.events";

    // Inventory events
    public static final String INVENTORY_LOW_STOCK = "inventory.low-stock";
    public static final String INVENTORY_OUT_OF_STOCK = "inventory.out-of-stock";
    public static final String INVENTORY_REPLENISHED = "inventory.replenished";

    // Price events
    public static final String PRICE_CHANGED = "price.changed";
    public static final String PROMOTION_STARTED = "promotion.started";
    public static final String PROMOTION_ENDED = "promotion.ended";

    // Finance events
    public static final String FINANCE_APPLICATION_SUBMITTED = "finance.application.submitted";
    public static final String FINANCE_DECISION = "finance.decision";

    // Loyalty events
    public static final String LOYALTY_POINTS_EARNED = "loyalty.points.earned";
    public static final String LOYALTY_TIER_CHANGED = "loyalty.tier.changed";

    // Notification events
    public static final String NOTIFICATION_SENT = "notification.sent";
    public static final String NOTIFICATION_FAILED = "notification.failed";

    // Reporting events
    public static final String REPORT_REQUESTED = "report.requested";
    public static final String REPORT_READY = "report.ready";

    // Queue names
    public static final String NOTIFICATION_QUEUE = "destore.notifications";
    public static final String INVENTORY_QUEUE = "destore.inventory";
    public static final String REPORTING_QUEUE = "destore.reporting";
}