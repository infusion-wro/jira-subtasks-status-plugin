var gadget;
var appLinkId;
var projectKey;
var epicCaption;

var atlassianBaseUrl;



function startGadget(baseUrl) {
    atlassianBaseUrl = baseUrl;
    initGadget();

//    AJS.$.ajax({
//        url: atlassianBaseUrl + "/rest/jiraanywhere/1.0/servers",
//        type: "GET",
//        dataType: "json",
//        contentType: "application/json",
//        success:
//            function (servers) {
//                appLinkId = servers[0].id;
//                initGadget();
//            }
//    });
}

function initGadget() {
                var gadget = AJS.Gadget({
                    baseUrl: atlassianBaseUrl,
                    useOauth: "/rest/gadget/1.0/currentUser",
                    view: {
                        template: function(args) {
                            console.log("args: ",args);
                            var gadget = this;

                            var projectKey = gadget.getPref("jiraProject");
                            var sprintsNo = gadget.getPref("sprintsNo");
                            var allSprints;


                            AJS.$.ajax({
                                //url: atlassianBaseUrl + "/rest/greenhopper/1.0/rapidviews/list?projectKey=PROJ ",
                                url: "/rest/greenhopper/1.0/rapidviews/list?projectKey="+projectKey,
                                type: "GET",
                                dataType: "json",
                                contentType: "application/json",
                                success:
                                    function (views) {
                                        //todo Here probably can be more than one projectview for project
                                        console.log("views: ",views);
                                        var viewId = views.views[0].id;
                                        AJS.$.ajax({
                                            //url: atlassianBaseUrl + "/rest/greenhopper/1.0/rapidviews/list?projectKey=PROJ ",
                                            url: "/rest/greenhopper/1.0/sprintquery/"+viewId+"?includeHistoricSprints=false&includeFutureSprints=true",
                                            type: "GET",
                                            dataType: "json",
                                            contentType: "application/json",
                                            success:
                                                function (sprints) {
                                                    allSprints=_.first(sprints.sprints,sprintsNo);
                                                    //todo Here probably can be more than one projectview for project
                                                    console.log("sprints ",sprints);
                                                    console.log("allSprints ",allSprints);
                                                    gadget.getView().html("<div>"+allSprints+"</div>");

                                                }
                                        });
                                    }
                            });


                            var projectList = AJS.$("<ul/>");

                            AJS.$(args.projectData.projects).each(function() {
                                projectList.append(
                                    AJS.$("<li/>").append(
                                        AJS.$("<a/>").attr({
                                            target: "_parent",
                                            title: gadgets.util.escapeString(this.key),
                                            href: atlassianBaseUrl+ "/browse/" + this.key
                                        }).text(this.name)
                                    )
                                );
                            });

                             projectKey = gadget.getPref("jiraProject");
                             console.log("JIRA project key:", projectKey);

                            gadget.getView().html("<div>"+allSprints+"</div>");
                            //gadget.getView().html(projectList);


                        },
                        args: [{
                            key: "projectData",
                            ajaxOptions: function() {
                                return {
                                    type: "GET",
                                    dataType: "json",
                                    contentType: "application/json",
                                    //url: "/rest/tutorial-gadget/1.0/projects.json"
                                    url: "/rest/api/2/search?jql=project%20%3D%20PROJ%20and%20Sprint%20%3D%201%20and%20type%20%3D%20Sub-task%20ORDER%20BY%20key%20asc&fields=assignee%2Cprogress%2Cproject%2Cissuetype%2Cstatus.json"
                                };
                            }
                        }]
                    },
                    config: {
                        descriptor: function(args){
                                return {
                                    fields:[
                                        {
                                            userpref: "jiraProject",
                                            type: "string",
                                            value: "",
                                            label: "jira project"
                                        },
                                        {
                                            userpref: "sprintsNo",
                                            type: "int",
                                            value: "",
                                            label: "sprints"
                                        },
                                        AJS.gadget.fields.nowConfigured()]
                                        }
                        }
                    }
                });
            }