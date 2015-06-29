//package util;
//
//import com.google.common.base.Function;
//import com.google.common.base.Joiner;
//import com.google.common.collect.Iterables;
//import com.google.common.collect.Maps;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang.StringUtils;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * Replacement for {@link com.atlassian.confluence.extra.jira.api.services.JqlBuilder} that seems to be extended with
// * support for notEmpty() and json formatted result.
// *
// * @author Wojciech Kaczmarek
// */
//public class JqlBuilder {
//
//    private static final String ISSUE_KEY_PARAM = "key";
//    private static final String ISSUE_TYPE_PARAM = "type";
//    private static final String ISSUE_STATUS_PARAM = "status";
//    private static final String ISSUE_STATUS_CATEGORY_PARAM = "statusCategory";
//    private static final String ISSUE_PROJECT_PARAM = "project";
//    private static final String ISSUE_AFFECTED_VERSION_PARAM = "affectedVersion";
//    private static final String ISSUE_FIXED_VERSION_PARAM = "fixVersion";
//
//    private static final String ISSUE_COMPONENT_PARAM = "component";
//    private static final String ISSUE_ASSIGNEE_PARAM = "assignee";
//    private static final String ISSUE_REPORTER_PARAM = "reporter";
//
//    private final List<String> notEmpty;
//    private final List<String> andExpressions;
//    private final Map<String, String> singleValueParamMap;
//    private final Map<String, String[]> multiValueParamMap;
//
//    public JqlBuilder() {
//        singleValueParamMap = Maps.newHashMap();
//        multiValueParamMap = Maps.newHashMap();
//        notEmpty = new ArrayList<String>();
//        andExpressions = new ArrayList<String>();
//    }
//
//    public JqlBuilder put(String key, String value) {
//        singleValueParamMap.put(key, value);
//        return this;
//    }
//
//    public JqlBuilder put(String key, String... values) {
//        multiValueParamMap.put(key, values);
//        return this;
//    }
//
//    public JqlBuilder issueKeys(String... issueKeyValues) {
//        put(ISSUE_KEY_PARAM, issueKeyValues);
//        return this;
//    }
//
//    public JqlBuilder issueTypes(String... issueTypes) {
//        put(ISSUE_TYPE_PARAM, issueTypes);
//        return this;
//    }
//
//    public JqlBuilder projectKeys(String... projectKeyValues) {
//        put(ISSUE_PROJECT_PARAM, projectKeyValues);
//        return this;
//    }
//
//    public JqlBuilder affectsVersions(String... affectsVersions) {
//        put(ISSUE_AFFECTED_VERSION_PARAM, affectsVersions);
//        return this;
//    }
//
//    public JqlBuilder components(String... components) {
//        put(ISSUE_COMPONENT_PARAM, components);
//        return this;
//    }
//
//    public JqlBuilder statuses(String... statuses) {
//        put(ISSUE_STATUS_PARAM, statuses);
//        return this;
//    }
//
//    public JqlBuilder statusCategories(String... statusCategories) {
//        put(ISSUE_STATUS_CATEGORY_PARAM, statusCategories);
//        return this;
//    }
//
//    public JqlBuilder fixVersions(String... fixedVersions) {
//        put(ISSUE_FIXED_VERSION_PARAM, fixedVersions);
//        return this;
//    }
//
//    public JqlBuilder assignees(String... assignees) {
//        put(ISSUE_ASSIGNEE_PARAM, assignees);
//        return this;
//    }
//
//    public JqlBuilder reporters(String... reporters) {
//        put(ISSUE_REPORTER_PARAM, reporters);
//        return this;
//    }
//
//    public JqlBuilder notEmpty(String field) {
//        notEmpty.add(field);
//        return this;
//    }
//
//    public JqlBuilder and(String expression) {
//        andExpressions.add(expression);
//        return this;
//    }
//
//    public String buildRawJql() {
//        if (MapUtils.isEmpty(singleValueParamMap) && MapUtils.isEmpty(multiValueParamMap)) {
//            throw new IllegalArgumentException("Builder have no any parameter");
//        }
//
//        StringBuffer paramString = new StringBuffer();
//
//        //build jqlMap
//        Joiner.MapJoiner joiner = Joiner.on(" AND ").withKeyValueSeparator("=");
//        paramString.append(joiner.join(singleValueParamMap));
//
//        //build jqlMapArray
//        if (MapUtils.isNotEmpty(multiValueParamMap)) {
//            if (MapUtils.isNotEmpty(singleValueParamMap)) {
//                paramString.append(" AND ");
//            }
//            Iterator<String> jqlSets = multiValueParamMap.keySet().iterator();
//            while (jqlSets.hasNext()) {
//                String key = jqlSets.next();
//                String inData = StringUtils.join(multiValueParamMap.get(key), ",");
//                paramString.append(key + " IN(");
//                paramString.append(inData);
//                paramString.append(")");
//                if (jqlSets.hasNext()) {
//                    paramString.append(" AND ");
//                }
//            }
//        }
//
//        if (notEmpty.size() > 0) {
//            String notEmptyClause = Joiner.on(" AND ").join(Iterables.transform(notEmpty, new Function<String, Object>() {
//                @Override
//                public Object apply(String input) {
//                    return MessageFormat.format("\"{0}\" is not empty", input);
//                }
//            }));
//
//            if (paramString.length() > 0) {
//                paramString.append(" AND ");
//            }
//
//            paramString.append(notEmptyClause);
//        }
//
//        if (andExpressions.size() > 0) {
//            String andExpressionsClause = Joiner.on(" AND ").join(andExpressions);
//
//            if (paramString.length() > 0) {
//                paramString.append(" AND ");
//            }
//
//            paramString.append(andExpressionsClause);
//        }
//
//        return paramString.toString();
//    }
//
//    public String build() {
//        return "{\"jql\":\"" + buildRawJql() + "\"}";
//    }
//
//    public String buildAndEncode() {
//        try {
//            return "jql=" + URLEncoder.encode(buildRawJql(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            throw new AssertionError("UTF-8 is not supported in system");
//        }
//    }
//
//}
