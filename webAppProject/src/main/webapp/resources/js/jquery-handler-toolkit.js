
//http://stackoverflow.com/questions/12214654/jquery-1-8-find-event-handlers
//http://james.padolsey.com/javascript/debug-jquery-events-with-listhandlers/

// $('*').listHandlers('*');
// $('a').listHandlers('onclick');

$(document).ready(function(){

$.fn.listHandlers = function(events) {
     this.each(function(i){
        var elem = this,
               // dEvents = $(this).data('events');
        dEvents = $._data($(this).get(0), "events");
        if (!dEvents) {return;}
        $.each(dEvents, function(name, handler){
            if((new RegExp('^(' + (events === '*' ? '.+' : events.replace(',','|').replace(/^on/i,'')) + ')$' ,'i')).test(name)) {
                $.each(handler,
                        function(i,handler){
                    //console.info(elem);
                    console.info(elem, '\n' + i + ': [' + name + '] : ' + handler.handler );
                });
            }
        });
    });
};


$.fn.hasHandlers = function(events,selector) {
   var result=false;

     this.each(function(i){
        var elem = this;       
        dEvents = $._data($(this).get(0), "events");
        if (!dEvents) {return false;}
        $.each(dEvents, function(name, handler){
            if((new RegExp('^(' + (events === '*' ? '.+' : events.replace(',','|').replace(/^on/i,'')) + ')$' ,'i')).test(name)) {
                $.each(handler,
                     function(i,handler){
			if (handler.selector===selector)
			result=true;
                });
            }
        });
    });
    return result;
};


});	
