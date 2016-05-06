function initZoomAndPanned(){
	jQuery.fn.imagePanAndZoom = function(imageURL,option){
	
		var mouseDownPan = false;
		var mouseXPan = 0;
		var mouseYPan = 0;
		var totalOffSetXPan = 0;
		var totalOffSetYPan = 0;
		var widthPercentPan = 100;
		var container = jQuery(this);
		var zoomContainer = null;
		var zoomTimeout = null;
		var img;
		var zoomTouch0 = null;
		var zoomTouch1 = null;
		var distanceToIncZoom = 30;
		if(option && (typeof option === 'string' || option instanceof String) && option.toLowerCase().indexOf("svg")>-1){
			img = jQuery("<div></div>");
		}else{
			img = jQuery("<img></img>");
		}
		//Init containers
		container.empty();
		container.unbind('mousedown touchstart mousemove touchmove touchend mouseup mouseout wheel');
		container.addClass('containerPanAndZoom noDraggablePanAndZoom');
		if(!(option && (typeof option === 'string' || option instanceof String) && option.toLowerCase().indexOf("svg")>-1)){
			img.attr("src", imageURL);
		}
		
		img.attr("style", "position:relative; width:100%;height:auto; left:0px;top:0px");
		img.attr('class','noDraggablePanAndZoom');

		container.append(img);
		if(option && (typeof option === 'string' || option instanceof String) && option.toLowerCase().indexOf("svg")>-1){
			img.load(imageURL,function(){
				jQuery(img).find("svg").attr("width","100%");
				jQuery(img).find("svg").attr("height","100%");
			});
		}
		function removeLastZoom(){
    			if(zoomContainer !== null){
			        zoomContainer.remove();
				zoomContainer = null;
			}
			if(zoomTimeout !== null){
				window.clearTimeout(zoomTimeout);
			        zoomTimeout = null;
			}
		}		

		
		function updateTotalOffSets(){
		    minX = (img.width()*-1)+50;
		    if(totalOffSetXPan < minX){
		        totalOffSetXPan = minX;
		    }
		    minY = (img.height()*-1)+50;
		    if(totalOffSetYPan < minY){
		        totalOffSetYPan = minY;
		    }
		    maxX = container.width()-50;
		    if(totalOffSetXPan > maxX){
		        totalOffSetXPan = maxX;
		    }
		    maxY = container.height()-50;
		    if(totalOffSetYPan > maxY){
		        totalOffSetYPan = maxY;
		    }
		    img.css('left',totalOffSetXPan+'px');
		    img.css('top',totalOffSetYPan+'px');
		}
    
		

		function updateZoom(){
			removeLastZoom();
    			zoomContainer = jQuery('<div>'+widthPercentPan+'%</div>');
    			zoomContainer.attr('class','zoomTextContainer');
    			container.append(zoomContainer);
    			zoomTimeout = window.setTimeout(removeLastZoom, 1200);
    			img.css('width',widthPercentPan+'%');
    			updateTotalOffSets();
		}

		container.on('mousedown',function(event){
    			if(event.preventDefault){
			        event.preventDefault()
			}
			mouseDownPan = true;
			mouseXPan = event.pageX;
			mouseYPan = event.pageY;
		});

		container.on('touchstart',function(event){
			event.stopImmediatePropagation();
			if(event.preventDefault){
			        event.preventDefault()
			}
			var ev = event.originalEvent;
			if (ev.targetTouches.length == 1) {
				var touch = ev.targetTouches[0];
				mouseDownPan = true;
				mouseXPan = touch.pageX;
				mouseYPan = touch.pageY;
			}
			if (ev.targetTouches.length == 2) {
				zoomTouch0 = ev.targetTouches[0];
				zoomTouch1 = ev.targetTouches[1];			
			}
		});

		container.on('mousemove',function(event){
			if(mouseDownPan === true){
		        	var offSetX = event.pageX - mouseXPan;
			        var offSetY = event.pageY - mouseYPan;
			        totalOffSetXPan += offSetX;
			        totalOffSetYPan += offSetY;
			        mouseXPan = event.pageX;
			        mouseYPan = event.pageY;
			        updateTotalOffSets();
			}
		});
		container.on('touchmove',function(event){
			event.stopImmediatePropagation();
			if(event.preventDefault){
			        event.preventDefault()
			}
			if(mouseDownPan === true){
				var ev = event.originalEvent;
				if (ev.targetTouches.length == 1) {
					var touch = ev.targetTouches[0];
			        	var offSetX = touch.pageX - mouseXPan;
				        var offSetY = touch.pageY - mouseYPan;
				        totalOffSetXPan += offSetX;
				        totalOffSetYPan += offSetY;
				        mouseXPan = touch.pageX;
				        mouseYPan = touch.pageY;
				        updateTotalOffSets();
				}
			}
			if (ev.targetTouches.length == 2) {
				var touch0 = ev.targetTouches[0];
				var touch1 = ev.targetTouches[1];
				var originalDistance = Math.sqrt(Math.pow(zoomTouch1.pageX - zoomTouch0.pageX,2)+Math.pow(zoomTouch1.pageY - zoomTouch0.pageY,2));
				var newDistance = Math.sqrt(Math.pow(touch1.pageX - touch0.pageX,2)+Math.pow(touch1.pageY - touch0.pageY,2));
				if(newDistance-originalDistance >= distanceToIncZoom){
					//Increment Zoom
					zoomTouch0 = touch0;
					zoomTouch1 = touch1;
					widthPercentPan += 25;
					updateZoom();
				}else if(originalDistance-newDistance >= distanceToIncZoom){
					//Decrement Zoom
					zoomTouch0 = touch0;
					zoomTouch1 = touch1;
					if(widthPercentPan > 25){
						widthPercentPan -= 25;
				        }
					updateZoom();
				}	
			}
		});

		container.on('touchend mouseup',function(event){
    			mouseDownPan = false;
		});

		container.on('mouseout',function(event){
    			mouseDownPan = false;
		});

		container.bind('wheel', function(event){
    			if(event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0){
				widthPercentPan += 25;
    			}else{
				if(event.originalEvent.wheelDelta){
	        			if(widthPercentPan > 25){
						widthPercentPan -= 25;
				        }
				}else{//For MAC Touchpad
					if(event.originalEvent.deltaY > 0){
						widthPercentPan += 25;
					}else if(event.originalEvent.deltaY < 0){
						if(widthPercentPan > 25){
							widthPercentPan -= 25;
				        	}
					}

				}
    			}
			updateZoom();
		});
	}
};

function runScriptZoomAndPanned() {
    if( window.jQuery ) {
        initZoomAndPanned();
    } else {
        window.setTimeout( runScriptZoomAndPanned, 100 );
    }
}
runScriptZoomAndPanned();
   
