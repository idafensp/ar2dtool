function initializeURIsToIgnoreOrInclude(urisContainer,ignoreOrIncludeList,uris,paramKey){
	jQuery(urisContainer).empty();
	jQuery(ignoreOrIncludeList).empty();
	jQuery.each(uris,function(i,uri){
		appendDivAddUri(uri,urisContainer,ignoreOrIncludeList,paramKey);
	});
	urisToRemove = [];
	jQuery.each(configJSON[paramKey],function(i,uri){
		if(uris.indexOf(uri)<0){
			urisToRemove.push(uri);
		}
	});
	jQuery.each(urisToRemove,function(i,uri){
		var index = configJSON[paramKey].indexOf(uri);
		if(index > -1){
			if(configJSON[paramKey].length!=1){
				configJSON[paramKey].splice( index, 1 );
			}else{
				configJSON[paramKey] = [];
			}
		}
	});
}

function appendDivAddUri(uri,urisContainer,ignoreOrIncludeList,paramKey){
	var div = document.createElement("DIV");
	var spanURI = document.createElement("DIV");
	var spanImage = document.createElement("DIV");
	var imgAdd = document.createElement("IMG");
	jQuery(spanURI).append(uri);
	jQuery(imgAdd).attr("src","resources/img/add.png");
	jQuery(spanImage).append(imgAdd);
	jQuery(div).append(spanURI);
	jQuery(div).append(spanImage);
	jQuery(urisContainer).append(div);
	jQuery(spanImage).click(function(){
		addUriToIgnoreOrInclude(spanImage,uri,paramKey,ignoreOrIncludeList);
	});
	if(configJSON[paramKey].indexOf(uri)>=0){
		addUriToIgnoreOrInclude(spanImage,uri,paramKey,ignoreOrIncludeList,true);
	}
}


function addUriToIgnoreOrInclude(spanImage,uri,paramKey,ignoreOrIncludeList,forceADD){
	if(configJSON[paramKey].indexOf(uri) < 0 || forceADD){
		jQuery(spanImage).addClass("buttonDisable");
		var divMinus = document.createElement("DIV");
		var spanURIMinus = document.createElement("DIV");
		var spanImageMinus = document.createElement("DIV");
		var imgAddMinus = document.createElement("IMG");
		jQuery(spanURIMinus).append(uri);
		jQuery(imgAddMinus).attr("src","resources/img/minus.png");
		jQuery(spanImageMinus).append(imgAddMinus);
		jQuery(divMinus).append(spanURIMinus);
		jQuery(divMinus).append(spanImageMinus);
		jQuery(ignoreOrIncludeList).append(divMinus);
		jQuery(spanImageMinus).click(function(){
			removeUriToIgnoreOrInclude(spanImage, uri, paramKey,divMinus);
		});
		if(!forceADD){
			configJSON[paramKey].push(uri);
		}
	}
}

function removeUriToIgnoreOrInclude(spanImage,uri,paramKey,divMinus){
	jQuery(divMinus).remove();
	var index = configJSON[paramKey].indexOf(uri);
	if(index > -1){
		if(configJSON[paramKey].length!=1){
			configJSON[paramKey].splice( index, 1 );
		}else{
			configJSON[paramKey] = [];
		}
	}
	jQuery(spanImage).removeClass("buttonDisable");
}

var equivalentELMap={};

function initializeEquivalentElementList(urisContainer,addedContainer,uris,paramKey){
	jQuery(urisContainer).empty();
	jQuery(addedContainer).empty();
	/*
	 * For TESTING
	 * configJSON[paramKey].push([uris[0],uris[1],uris[2],uris[3]]);
	configJSON[paramKey].push(["noAnadir",uris[1],uris[2],uris[3]]);*/
	jQuery.each(configJSON[paramKey],function(i,list){
		var masterUri=null;
		jQuery.each(list,function(i2,uri){
			if(uris.indexOf(uri)>=0){
				if(i2==0){
					masterUri=uri;
					equivalentELMap[masterUri]=[];
				}else if(masterUri && equivalentELMap[masterUri]){
					equivalentELMap[masterUri].push(uri);
				}
			}
		});
	});
	jQuery.each(uris,function(i,uri){
		appendDivAddUriEquivalent(uri,uris,urisContainer,addedContainer,paramKey)
	});
}

function appendDivAddUriEquivalent(uri,uris,urisContainer,addedContainer,paramKey){
	var div = document.createElement("DIV");
	var spanURI = document.createElement("DIV");
	var spanImage = document.createElement("DIV");
	var imgEdit = document.createElement("IMG");	
	jQuery(spanURI).append(uri);
	jQuery(imgEdit).attr("src","resources/img/edit.png");
	jQuery(spanImage).append(imgEdit);
	jQuery(div).append(spanURI);
	jQuery(div).append(spanImage);
	jQuery(div).addClass("equivalentUri");
	jQuery(urisContainer).append(div);
	jQuery(div).click(function(){
		changeAddedContainerEquivalent(uri,uris,addedContainer,imgEdit,paramKey);
		jQuery(urisContainer).find(".equivalentUri").removeClass("editing");
		jQuery(div).addClass("editing");
		lastClickedContainer = div;
	});
}

function changeAddedContainerEquivalent(uri,uris,addedContainer,imgEdit,paramKey){
	jQuery(addedContainer).empty();
	jQuery.each(uris,function(i,addUri){
		if(uri != addUri){
			var div = document.createElement("DIV");
			var spanURI = document.createElement("DIV");
			var spanImage = document.createElement("DIV");
			jQuery(spanURI).append(addUri);
			jQuery(div).append(spanURI);
			jQuery(div).append(spanImage);
			jQuery(spanImage).addClass('equivalentCheckbox');
			jQuery(addedContainer).append(div);
			if(equivalentELMap[uri] && equivalentELMap[uri].length>1 && equivalentELMap[uri].indexOf(addUri)>=0){
				jQuery(spanImage).addClass('selected');
			}
			jQuery(spanImage).click(function(event){
				if(jQuery(spanImage).hasClass('selected')){
					jQuery(spanImage).removeClass('selected');
					if(equivalentELMap[uri] && equivalentELMap[uri].indexOf(addUri)>=0){
						if(equivalentELMap[uri].length>1){
							var index = equivalentELMap[uri].indexOf(addUri);
							equivalentELMap[uri].splice( index, 1 );
						}else{
							equivalentELMap[uri] = [];
							jQuery(imgEdit).attr('src','resources/img/edit.png');
						}
					}
					if(equivalentELMap[uri] && equivalentELMap[uri].length==0){
						delete equivalentELMap[uri];
					}
					saveEquivalentMapToConfig(paramKey);
				}else{
					jQuery(spanImage).addClass('selected');
					jQuery(imgEdit).attr('src','resources/img/edit_edited.png');
					if(!equivalentELMap[uri]){
						equivalentELMap[uri]=[];
					}
					equivalentELMap[uri].push(addUri);
					saveEquivalentMapToConfig(paramKey);
				}
			});
		}
	});
}

function saveEquivalentMapToConfig(paramKey){
	configJSON[paramKey]=[];
	jQuery.each(equivalentELMap, function(key,equivalentArray){
		var array = [key];
		jQuery.each(equivalentArray,function(i,uri){
			array.push(uri);
		});

		if(equivalentELMap[key].length>0){
			configJSON[paramKey].push(array);
		}
	});
}

var specialElementListMap = {};
var specialListActualURI = '';
var specialElementListRelationImg = {};

function initializeSpecialElementList(urisContainer,editContainer,uris,paramKey){
	jQuery(urisContainer).empty();
	initializeSpecialColorChosen();
	var firtsElement=true;
	jQuery.each(configJSON[paramKey],function(i,list){
		var masterUri=null;
		jQuery.each(list,function(i2,prop){
			if(uris.indexOf(prop)>=0){
				if(i2==0){
					masterUri=prop;
					specialElementListMap[masterUri]={};
					specialElementListMap[masterUri]['edit'] = true;
				}else if(i2==1){
					specialElementListMap[masterUri]['shape'] = prop;
				}else if(i2==2){
					specialElementListMap[masterUri]['color'] = prop;
				}
			}
		});
	});
	jQuery.each(uris,function(i,uri){
		appendDivAddUriSpecialList(uri,uris,urisContainer,editContainer,paramKey,firtsElement);
		firtsElement = false;
	});
	jQuery(editContainer).find('[data-specialList=edit]').on('click tap',function(){
		if(specialElementListMap[specialListActualURI]){
			if(specialElementListMap[specialListActualURI]['edit']){
				specialElementListMap[specialListActualURI]['edit'] = false;
				jQuery(specialElementListRelationImg[specialListActualURI]).attr('src','resources/img/edit.png');
				changeEditContainerEquivalent(specialListActualURI,uris,editContainer,null,paramKey);
			}else{
				specialElementListMap[specialListActualURI] = {};
				specialElementListMap[specialListActualURI]['edit'] = true;
				jQuery(specialElementListRelationImg[specialListActualURI]).attr('src','resources/img/edit_edited.png');
				specialElementListMap[specialListActualURI]['color'] = jQuery(editContainer).find('[data-specialList=color]').val();
				specialElementListMap[specialListActualURI]['shape'] = jQuery(editContainer).find('[data-specialList=shape]').val();
				changeEditContainerEquivalent(specialListActualURI,uris,editContainer,null,paramKey);
			}
		}else{
			specialElementListMap[specialListActualURI] = {};
			specialElementListMap[specialListActualURI]['edit'] = true;
			jQuery(specialElementListRelationImg[specialListActualURI]).attr('src','resources/img/edit_edited.png');
			specialElementListMap[specialListActualURI]['color'] = jQuery(editContainer).find('[data-specialList=color]').val();
			specialElementListMap[specialListActualURI]['shape'] = jQuery(editContainer).find('[data-specialList=shape]').val();
			changeEditContainerEquivalent(specialListActualURI,uris,editContainer,null,paramKey);
		}
		saveSpecialMapToConfig(paramKey);
	});
	jQuery(editContainer).find('[data-specialList=shape]').on('change',function(){
		if(specialElementListMap[specialListActualURI] && specialElementListMap[specialListActualURI]['edit']){
			specialElementListMap[specialListActualURI]['shape'] = jQuery(editContainer).find('[data-specialList=shape]').val();
		}
		saveSpecialMapToConfig(paramKey);
	});
	jQuery(editContainer).find('[data-specialList=color]').on('change',function(){
		if(specialElementListMap[specialListActualURI] && specialElementListMap[specialListActualURI]['edit']){
			specialElementListMap[specialListActualURI]['color'] = jQuery(editContainer).find('[data-specialList=color]').val();
		}
		saveSpecialMapToConfig(paramKey);
	});
}

function resetSpecialColorChosen(val,editContainer,paramKey){
	jQuery('#toResetSpecialColor').empty();
	select = jQuery("<select name='SelectSpecialColor'  data-specialList='color'><option data-img-src='black' data-specialList='black' class='selected' value='black' data-selected=''>black</option><option data-img-src='red' data-specialList='red'  class='notSelected' value='red'>red</option><option data-img-src='blue' data-specialList='blue' class='notSelected' value='blue'>blue</option><option data-img-src='green' data-specialList='green' class='notSelected' value='green'>green</option><option data-img-src='orange' data-specialList='orange' class='notSelected' value='orange'>orange</option><option data-img-src='yellow' data-specialList='yellow' class='notSelected' value='yellow'>yellow</option></select>");
	jQuery('#toResetSpecialColor').append(select);
	if(val){
		select.val(val);
	}
	if(editContainer){
		jQuery(editContainer).find('[data-specialList=color]').on('change',function(){
			if(specialElementListMap[specialListActualURI] && specialElementListMap[specialListActualURI]['edit']){
				specialElementListMap[specialListActualURI]['color'] = jQuery(editContainer).find('[data-specialList=color]').val();
			}
			saveSpecialMapToConfig(paramKey);
		});
	}
	initializeSpecialColorChosen();
}

function initializeSpecialColorChosen(){
	jQuery("select[name=SelectSpecialColor]").chosen(
			{'width':'auto !important;max-width:200px !important;min-width:90px !important;margin-bottom:0px;',
				'html_template': '<span class="colorSelect" style="background-color:{url};"></span>',
				'template': '<div class="colorSelect" style="background-color:{url};"></div>',
				'disable_search':true});
	
}

function appendDivAddUriSpecialList(uri,uris,urisContainer,editContainer,paramKey,firtsElement){
	var div = document.createElement("DIV");
	var spanURI = document.createElement("DIV");
	var spanImage = document.createElement("DIV");
	var imgEdit = document.createElement("IMG");
	specialElementListRelationImg[uri] = imgEdit;
	jQuery(spanURI).append(uri);
	jQuery(imgEdit).attr("src","resources/img/edit.png");
	jQuery(spanImage).append(imgEdit);
	jQuery(div).append(spanURI);
	jQuery(div).append(spanImage);
	jQuery(div).addClass("equivalentUri");
	jQuery(urisContainer).append(div);
	if(firtsElement){
		changeEditContainerEquivalent(uri,uris,editContainer,imgEdit,paramKey);
		jQuery(urisContainer).find(".equivalentUri").removeClass("editing");
		jQuery(div).addClass("editing");
		lastClickedContainer = div;
		specialListActualURI = uri;
	}
	jQuery(div).click(function(){
		changeEditContainerEquivalent(uri,uris,editContainer,imgEdit,paramKey);
		jQuery(urisContainer).find(".equivalentUri").removeClass("editing");
		jQuery(div).addClass("editing");
		lastClickedContainer = div;
		specialListActualURI = uri;
	});
}

function changeEditContainerEquivalent(uri,uris,editContainer,imgEdit,paramKey){
	if(specialElementListMap[uri]){
		if(specialElementListMap[uri]['shape']){
			jQuery(editContainer).find('[data-specialList=edit]').addClass('selected');
			jQuery(editContainer).find('[data-specialList=shapeContainer]').removeClass('disableSpecial');
			jQuery(editContainer).find('[data-specialList=shape]').val(specialElementListMap[uri]['shape']);
		}else{
			jQuery(editContainer).find('[data-specialList=shapeContainer]').addClass('disableSpecial');
		}
		if(specialElementListMap[uri]['color']){
			jQuery(editContainer).find('[data-specialList=edit]').addClass('selected');
			jQuery(editContainer).find('[data-specialList=colorContainer]').removeClass('disableSpecial');
			resetSpecialColorChosen(specialElementListMap[uri]['color'],editContainer,paramKey);
		}else{
			jQuery(editContainer).find('[data-specialList=colorContainer]').addClass('disableSpecial');
		}
		if(specialElementListMap[uri]['edit']){
			jQuery(editContainer).find('[data-specialList=edit]').addClass('selected');
		}else{
			jQuery(editContainer).find('[data-specialList=edit]').removeClass('selected');
			jQuery(editContainer).find('[data-specialList=shapeContainer]').addClass('disableSpecial');
			jQuery(editContainer).find('[data-specialList=colorContainer]').addClass('disableSpecial');
		}		
	}else{
		jQuery(editContainer).find('[data-specialList=edit]').removeClass('selected');
		jQuery(editContainer).find('[data-specialList=shapeContainer]').addClass('disableSpecial');
		jQuery(editContainer).find('[data-specialList=colorContainer]').addClass('disableSpecial');
		resetSpecialColorChosen('black',editContainer,paramKey);
		jQuery(editContainer).find('[data-specialList=shape]').val('rectangle');
	}
}

function saveSpecialMapToConfig(paramKey){
	configJSON[paramKey]=[];
	jQuery.each(specialElementListMap, function(key,specialObject){
		if(specialObject['edit'] && specialObject['color'] && specialObject['shape']){
			var array = [key,specialObject['shape'],specialObject['color']];
			configJSON[paramKey].push(array);
		}
	});
}


function ajaxGetALLUris(){
	//URI for getAllUris
	//webapi/methods/getAllUris
	ajaxGet('webapi/methods/getAllUris',{}
	,function done(data){
		if(!isError(data)){
    		var response = data['response'];
    		if(response){
    			var uris = response;
    			initializeURIsToIgnoreOrInclude(jQuery("#includeOnlyElementListURIS"),jQuery("#includeOnlyElementListAdded"),uris,'includeOnlyElementList');
    			initializeURIsToIgnoreOrInclude(jQuery("#ignoreElementListURIS"),jQuery("#ignoreElementListAdded"),uris,'ignoreElementList')
    			initializeEquivalentElementList(jQuery("#equivalentElementListURIS"),jQuery("#equivalentElementListAdded"),uris,'equivalentElementList');
    			initializeSpecialElementList(jQuery("#specialElementListURIS"),jQuery("#specialElementListEdit"),uris,'specialElementsList');
    		}else{
    			swal("Error on JSON of getAllUris. JSON response does not contains the key: response",error,"error");
    		}
    	}
	}
	,function error(error){
		console.log("Get URIs for lists error:"+error);
		swal("getAllUrisError",error,"error");
	})
}

