var gadget;
var appLinkId;
var projectKey;
var epicCaption;

var atlassianBaseUrl;

// configure _.template to use moustache syntax
_.templateSettings = {
    interpolate: /\{\{(.+?)\}\}/g
};

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
                            var gadget = this;

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

                            gadget.getView().html("<div>"+args.projectData.projects+"</div>");
                            gadget.getView().html(projectList);


                        },
                        args: [{
                            key: "projectData",
                            ajaxOptions: function() {
                                return {
                                    url: "/rest/tutorial-gadget/1.0/projects.json"
                                };
                            }
                        }]
                    }
                });
            }