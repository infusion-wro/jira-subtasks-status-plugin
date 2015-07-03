var gadget;
var projectKey;

var atlassianBaseUrl;
var gadget;


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


function displayInGadget(args){
    var content = Infusion.SubtasksStatus.Templates.chartBox({project: args});
    gadget.getView().html(content);

    gadgets.window.adjustHeight();
}
function initGadget() {
                gadget = AJS.Gadget({
                    baseUrl: atlassianBaseUrl,
                    useOauth: "/rest/gadget/1.0/currentUser",
                    view: {
                        template: function(args) {
                            console.log("args: ",args);
                            var gadget = this;

                            projectKey = gadget.getPref("jiraProject");
                            var sprintsNo = gadget.getPref("sprintsNo");
                            console.log('projectKey ',projectKey)
                            console.log('sprintsNo ',sprintsNo)
                            var allSprints;


                            AJS.$.ajax({
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
                                            url: "/rest/greenhopper/1.0/sprintquery/"+viewId+"?includeHistoricSprints=false&includeFutureSprints=true",
                                            type: "GET",
                                            dataType: "json",
                                            contentType: "application/json",
                                            success:
                                                function (argSprints) {
                                                    var ids =_.first(argSprints.sprints,sprintsNo).map(function(sprint){return sprint.id});
                                                    var sprints = AJS.$("<div/>");
                                                    var results = [];
                                                    for(i=0; i<sprintsNo;i++){
                                                        var sp = argSprints.sprints[i];
                                                        var id = sp.id;
                                                        var name = sp.name;
                                                        var result = {
                                                            sprintId: id,
                                                            sprintName: name,
                                                            };
                                                        console.log('sp ',name,' ',sp)
                                                        sprints.append(AJS.$("<b/>").text(name))
                                                        AJS.$.ajax({
                                                            url: "/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId="+argSprints.rapidViewId+"&sprintId="+id,
                                                            type: "GET",
                                                            async:false,
                                                            dataType: "json",
                                                            contentType: "application/json",
                                                            success:
                                                                function (args) {
                                                                    result.startDate = args.sprint.startDate;
                                                                    result.endDate = args.sprint.endDate;
                                                                    result.state= args.sprint.state;
                                                                    console.log('sprint report ',args.sprint)
                                                                    sprints.append(" start date: "+args.sprint.startDate+" end date: "+args.sprint.endDate)
                                                                }

                                                        })

                                                        AJS.$.ajax({
                                                            url: "/rest/api/2/search?jql=project%20%3D%20"+projectKey+"%20and%20Sprint%20%3D%20"+id+"%20and%20type%20%3D%20Sub-task%20ORDER%20BY%20key%20asc&fields=assignee%2Cprogress%2Cproject%2Cissuetype%2Cstatus%2Caggregatetimeoriginalestimate%2Caggregatetimeestimate%2Caggregatetimespent",
                                                            type: "GET",
                                                            async:false,
                                                            dataType: "json",
                                                            contentType: "application/json",
                                                            success:
                                                                function (arg) {
                                                                    console.log('issues for sprint ',id,' :',arg.issues)
                                                                    var allsubtasks =_.map(arg.issues,function(issue){
                                                                                        return {
                                                                                            assigned: issue.fields.assignee.displayName,
                                                                                            orgTime: issue.fields.aggregatetimeoriginalestimate,
                                                                                            remaining: issue.fields.aggregatetimeestimate,
                                                                                            total: issue.fields.progress.total,
                                                                                            logged: issue.fields.aggregatetimespent,
                                                                                            avatarUrl: issue.fields.assignee.avatarUrls['32x32']
                                                                                            };
                                                                        });
                                                                    var subtasks=_.groupBy(allsubtasks,function(subtask){return subtask.assigned;})
                                                                    var grouped = _.map(subtasks, function(a){
                                                                        console.log(a);
                                                                        return _.reduce(a,function(memo,fold){
                                                                            return {
                                                                                assigned: memo.assigned,
                                                                                orgTime: memo.orgTime + fold.orgTime,
                                                                                remaining: memo.remaining + fold.remaining,
                                                                                total: memo.total + fold.total,
                                                                                logged: memo.logged + fold.logged,
                                                                                avatarUrl: memo.avatarUrl
                                                                                };
                                                                            });
                                                                        })

                                                                    var userList = AJS.$("<ul/>");
                                                                    AJS.$(grouped).each(function(group) {
                                                                        console.log('group',this.assigned)
                                                                        userList.append(
                                                                            AJS.$("<li/>").text(this.assigned +" estimated time: "+this.orgTime/3600 + " remainging: "+this.remaining/3600))

                                                                    });
                                                                    result.userStats= grouped;
                                                                    //results[i] = {sprintId: id, sprintName: name, userStats: grouped};
                                                                    results[i] = result;
                                                                    sprints.append(userList);


                                                                }
                                                        });
                                                    }

                                                    var displayInfo ={projectKey: projectKey, sprints: results};
                                                    AJS.$.ajax({
                                                        url: "/rest/api/2/project/"+projectKey,
                                                        type: "GET",
                                                        async:false,
                                                        dataType: "json",
                                                        contentType: "application/json",
                                                        success:
                                                            function (args) {
                                                                displayInfo.projectName = args.name;
                                                            }
                                                            });
                                                    console.log('displayInfo: ',displayInfo);

                                                    displayInGadget(displayInfo);
                                                    //displayInGadget(sprints.append(Infusion.SubtasksStatus.Templates.chartBox({keyValue: 'key', keyLabel:'label', description:'desc'})));
                                                },
                                             error: function(args){
                                                 console.log('Error while retrieving sprints for rapidview: ',args.responseText);
                                                 displayInGadget(AJS.$("<div/>").text('Error while retrieving sprints for rapidview: ' + args.responseText));

                                             }
                                        });
                                    },
                                error: function(args){
                                    console.log('Error while retrieving rapidviews for project, args: ',args.responseText);
                                    displayInGadget(AJS.$("<div/>").text('Error while retrieving rapidviews for project, args: '+args.responseText));

                                }
                            });



                        }
                        ,
                        args: [{
//                            key: "projectData",
//                            ajaxOptions: function() {
//                                return {
//                                    type: "GET",
//                                    dataType: "json",
//                                    contentType: "application/json",
//                                };
//                            }
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