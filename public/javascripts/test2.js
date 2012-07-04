

Link = Backbone.View.extend({
	initialize: function(){
        alert("Alerts suck.");
    },
  tagName: "li",

  className: "mlink",

  events: {
    "click .delete":        "destroy",
  },

  destroy: function() {
	  alert("test");
  }
});

var l = new Link();