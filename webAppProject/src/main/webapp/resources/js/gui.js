var leftMenuOpen = false;

function changeStateLeftMenu(){
	if(leftMenuOpen === true){
		closeLeftMenu();
	} else {
		openLeftMenu();
	}
}

function openLeftMenu(){
	jQuery("#dropMenuContentContainer").removeClass('dropMenuContentContainerClose');
	jQuery("#dropMenuContentContainer").addClass('dropMenuContentContainerOpen');
	jQuery("#dropMenuContainer").removeClass('dropMenuContainerClose');
	jQuery("#dropMenuContainer").addClass('dropMenuContainerOpen');
	jQuery("#droprightClickMenuImg").css('display','none');
	jQuery("#dropleftClickMenuImg").css('display','block');
	leftMenuOpen = true;
}

function closeLeftMenu(){
	jQuery("#dropMenuContentContainer").removeClass('dropMenuContentContainerOpen');
	jQuery("#dropMenuContentContainer").addClass('dropMenuContentContainerClose');
	jQuery("#dropMenuContainer").removeClass('dropMenuContainerOpen');
	jQuery("#dropMenuContainer").addClass('dropMenuContainerClose');
	jQuery("#droprightClickMenuImg").css('display','block');
	jQuery("#dropleftClickMenuImg").css('display','none');
	leftMenuOpen = false;
}

function generateImage(){
	jQuery('#imageContainerZoomAndPan').imagePanAndZoom('resources/img/fondo.png');
}

function bindEvents(){
	var touch=null;
	var tryToOpenOrClose = false;
	var abriendo = false;
	var cerrando = false;
	jQuery("#mobileLeftMenuEvents").bind("touchstart",function(event){
		event.stopImmediatePropagation();
		if(event.preventDefault){
		        event.preventDefault();
		}
		var ev = event.originalEvent;
		if (ev.targetTouches.length == 1) {
			touch = ev.targetTouches[0];
		}
	});
	jQuery("#mobileLeftMenuEvents").bind("touchmove",function(event){
		event.stopImmediatePropagation();
		if(event.preventDefault){
		        event.preventDefault();
		}
		var ev = event.originalEvent;
		if (ev.targetTouches.length == 1) {
			var distance = ev.targetTouches[0].pageX - touch.pageX;
			if(distance > 0){
				if(!cerrando){
					abriendo = true;
					if(Math.abs(distance) < 0.7*jQuery("#dropMenuContainer").width()){			
						var px = -jQuery("#dropMenuContainer").width()+jQuery("#mobileLeftMenuEvents").width()+Math.abs(distance);					
						jQuery("#dropMenuContainer").css("left",px+"px");
						tryToOpenOrClose = true;
					}else{
						jQuery("#dropMenuContainer").css("left","");
						openLeftMenu();
						tryToOpenOrClose = false;
					}
				}
			}else if(distance < 0){
				if(!abriendo){
					cerrando = true;
					if(Math.abs(distance) < 0.7*jQuery("#dropMenuContainer").width()){
						var px = -Math.abs(distance);		
						jQuery("#dropMenuContainer").css("left",px+"px");
						tryToOpenOrClose = true;
					}else{
						jQuery("#dropMenuContainer").css("left","");
						closeLeftMenu();
						tryToOpenOrClose = false;
					}
				}
			}
		}
	});
	jQuery("#mobileLeftMenuEvents").bind("touchend",function(event){
		event.stopImmediatePropagation();
		if(event.preventDefault){
		        event.preventDefault();
		}
		abriendo = false;
		cerrando = false;
		if (tryToOpenOrClose) {
			jQuery("#dropMenuContainer").css("left","");
		}
	});
}

window.onload = function(){
	if( window.jQuery ) {
		bindEvents();
	} else {
		window.setTimeout( runScript, 100 );
	}
};


