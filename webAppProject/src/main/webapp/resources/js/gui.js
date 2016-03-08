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
	jQuery('#imageContainerZoomAndPan').imagePanAndZoom('');
	ajax('webapi/methods/generateImage',{config:JSON.stringify(configJSON)},function(data){
		if(!isError(data)){
			jQuery('#imageContainerZoomAndPan').imagePanAndZoom('webapi/methods/getImage?d='+new Date().getTime());
		}
	},function(error){
		swal("Upload error",error,"error");
	});
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

function isArray(object){
	if(object === null){
		return false;
	}
	return object.constructor === [].constructor;
}

function isObject(object){
	if(object === null){
		return false;
	}
	return object.constructor === {}.constructor;
}

function isString(object){
	if(object === null){
		return false;
	}
	return object.constructor === "".constructor;
}

function addAttributes(container,json){
	jQuery.each(json["css"],function(i,css){
		jQuery(container).css(i,css);
	});
	jQuery.each(json["attr"],function(i,attr){
		jQuery(container).attr(i,attr);
	});
	jQuery.each(json["class"],function(i,classe){
		jQuery(container).addClass(classe);
	});
}

function drawConfig(container,configJSON,configTaxonomyJSON){
	if(isArray(configTaxonomyJSON)){
		jQuery.each(configTaxonomyJSON,function(i,obj){
			if(obj["type"] === "CONTAINER"){
				var newContainer = document.createElement(obj["container"]);
				container.appendChild(newContainer);
				if(obj["param"]){
					jQuery(newContainer).attr("name",obj["param"]);
				}
				if(obj["attributes"]){
					addAttributes(newContainer,obj["attributes"]);
				}
				if(obj["content"]){
					drawConfig(newContainer,configJSON[obj["param"]],obj["content"]);
				}
			}
			if(obj["type"] === "VALUE"){
				
			}
		});
	}else{
		window.alert("Error in generate config the JSON: "+configTaxonomyJSON+" in not a array");
	}
}

function getDefaultConfig(){
	
}
var configTaxonomyJSON=[ { "param":"keys", "container":"DIV", "type":"CONTAINER", "content": [ { "param":"imageSize", "container":"DIV", "type":"CONTAINER", "content":[{"type":"value","container":"DIV","attributes":{"css":{},"attr":{"contenteditable":"true"},"class":{}}}] }, { "param":"arrowColor", "container":"CHECKBOX", "type":"CONTAINER", "content":[{"container":"DIV","attributes":{"css":{},"attr":{"contenteditable":"true"},"class":{}}}] } ] } ];
var configJSON={"equivalentElementList":[],"ignoreElementList":["http://www.w3.org/2000/01/rdf-schema#subClassOf"],"includeOnlyElementList":[],"keys":{"arrowColor":"black","literalShape":"rectangle","individualShape":"rectangle","classShape":"rectangle","nodeNameMode":"localname","arrowhead":"normal","literalColor":"black","rankdir":"LR","classColor":"orange","arrowdir":"forward","ignoreRdfType":"true","ignoreLiterals":"true","individualColor":"black","imageSize":"1501","arrowtail":"normal","synthesizeObjectProperties":"true"},"specialElementsList":[]};
//var configJSON={"equivalentElementList":[],"ignoreElementList":["http://www.w3.org/2000/01/rdf-schema#subClassOf"],"includeOnlyElementList":[],"specialElementsList":[],"keys":{"arrowColor":"green","literalShape":"ellipse","individualShape":"triangle","classShape":"diamond","nodeNameMode":"fulluri","arrowhead":"odot","literalColor":"orange","rankdir":"TB","classColor":"blue","arrowdir":"forward","ignoreRdfType":"true","ignoreLiterals":"true","individualColor":"red","imageSize":"1501","arrowtail":"dot","synthesizeObjectProperties":"true"}};
function generateConfig(config){
	//#dropMenuContentContainer #configContainer
	//var container = document.getElementById("configContainer");
	//drawConfig(container, configJSON, configTaxonomyJSON)
	putConfigValuesToConfigView(configJSON,jQuery("#configContainer"));
	bindConfigEvents();
}

function putConfigValuesToConfigView(json,container){
	jQuery.each(json,function(key,value){
		if(isObject(json[key])){
			putConfigValuesToConfigView(json[key], jQuery(container).find('[configContainer='+key+']')[0]);
		}else{
			changeValueOfContainer(jQuery(container).find('[configParam='+key+'] [configValue]')[0], value);
		}
	});
}

function changeValueOfContainer(container,value){
	if(jQuery(container).is("option")){
		var select = jQuery(container).parent()[0];
		jQuery(select).val(value);
	}else if(jQuery(container).is("input[type=checkbox]")){
		jQuery(container).prop( "checked", value );
	}else if(jQuery(container).is("input")){
		if(!isString(value)){
			jQuery(container).val(JSON.stringify(value));
		}else{
			jQuery(container).val(value);
		}
	}else{
		var parent = jQuery(container).parent()[0];
		jQuery(parent).children("[configValue]").removeClass("selected").addClass("notSelected");
		jQuery(parent).children('[configValue='+value+']').removeClass("notSelected").addClass("selected");
	}
}

function bindConfigEvents(){
	jQuery("#configContainer").find("[configParam]").each(function(i,param){
		jQuery(param).find("[configValue]").each(function(i,value){
			if(jQuery(value).is("option")){
				if(!jQuery(jQuery(value).parent()[0]).hasHandlers("change")){
					jQuery(jQuery(value).parent()[0]).change(function(){
						configJSON["keys"][jQuery(param).attr("configParam")]=jQuery(this).val();
					});
				}
			}else if(jQuery(value).is("input[type=checkbox]")){
				if(!jQuery(jQuery(value).parent()[0]).hasHandlers("change")){
					jQuery(jQuery(value).parent()[0]).change(function(){
						configJSON["keys"][jQuery(param).attr("configParam")]=jQuery(value).is(":checked");
					});
				}
			}else if(jQuery(value).is("input")){
				if(!jQuery(value).hasHandlers("change")){
					jQuery(value).change(function(){
						var parentContainerID = jQuery(jQuery(value).closest("[configContainer]")[0]).attr("configContainer");
						if(!jQuery(jQuery(value).parent()[0]).is("[configContainer]")){
							configJSON[parentContainerID][jQuery(param).attr("configParam")]=JSON.parse(jQuery(value).val());
						}else{
							configJSON[parentContainerID]=JSON.parse(jQuery(value).val());
						}
					});
				}
			}else{
				jQuery(value).click(function(e){
					configJSON["keys"][jQuery(param).attr("configParam")]=jQuery(value).attr("configValue");
					jQuery(param).find("[configValue]").each(function(i,toChange){
						jQuery(toChange).removeClass("selected").addClass("notSelected");
					});
					jQuery(value).removeClass("notSelected").addClass("selected");
				});
			}
		});
	});
}

function ajax(urlString,data,funcionDone,funcionError){
	jQuery.ajax({
		url: urlString,
	    type: "POST",
	    data : data,
	    contentType: 'application/x-www-form-urlencoded'
	}).done(funcionDone).error(funcionError);
}

function ajaxUploadFile(idContainer){
	var urlString="webapi/methods/uploadFile";
	var fd = new FormData(document.getElementById(idContainer));
    jQuery.ajax({
      url: urlString,
      type: "POST",
      data: fd,
      enctype: 'multipart/form-data',
      processData: false,  // tell jQuery not to process the data
      contentType: false   // tell jQuery not to set contentType
    }).done(function( data ) {
    	if(!isError(data)){
    		swal("Uploaded ttl file", "The file has been uploaded.", "success");
    	}
    }).error(function(error){
    	swal("Upload error",error,"error");
    });
}

function isError(response){
	if(response["errorResponse"]){
		if(response["idErrorMessage"]){
			swal("Service error",response["idErrorMessage"]+":"+response["errorMessage"]);
		}else{
			swal("Service error",response["errorMessage"]);
		}
		return true;
	}
	return false;
}

window.onload = function(){
	if( window.jQuery ) {
		bindEvents();
		generateConfig();
		var formText = '<form id="ttlFileForm" action="webapi/methods/uploadFile" method="post" enctype="multipart/form-data"><input type="file" name="file" style="display:block;border:0px;margin-top:10px;">';
		swal({   title: "Upload a ttl file",   text: formText,allowEscapeKey:false,html:true,   type: null,   showCancelButton: false,   closeOnConfirm: false,   showLoaderOnConfirm: true, }, function(){
			ajaxUploadFile("ttlFileForm");
		});
	} else {
		window.setTimeout( runScript, 100 );
	}
};


