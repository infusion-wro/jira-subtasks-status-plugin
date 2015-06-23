package util;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;

import java.util.Map;

public class Util {

    private Util() {}

    public static Object getStringValueByPath(final Map<String, Object> map, final String... path) {
        Map<String, Object> currentMap = map;

        for (int i = 0; i < path.length - 1; i++) {
            String pathElement = path[i];

            if (!(currentMap.get(pathElement) instanceof Map)) {
                return null;
            }

            currentMap = (Map<String, Object>) currentMap.get(pathElement);
        }

        String propertyPath = path[path.length - 1];

        return currentMap.get(propertyPath);
    }
//
//    public static <K, V> V ensureEntry(Map<K, V> map, K key, Supplier<V> valueSupplier) {
//        if (!map.containsKey(key)) {
//            map.put(key, valueSupplier.get());
//        }
//
//        return map.get(key);
//    }
//
//    public static <T> T buildAggregate(IssuesAggregator<T> aggregator, String applicationId, Iterable<JiraIssue> issues) {
//        IssuesAggregationBuilder<T> builder = aggregator.getAggregationBuilder(applicationId);
//
//        for(JiraIssue issue: issues) {
//            builder.feed(issue);
//        }
//
//        return builder.build();
//    }

    public static ApplicationLink getApplicationLink(ApplicationLinkService appLinkService, String applicationId) throws TypeNotInstalledException {
                 return applicationId == null ?
                appLinkService.getPrimaryApplicationLink(JiraApplicationType.class) :
                appLinkService.getApplicationLink(new ApplicationId(applicationId));
    }
}
