<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang='en-gb' class="no-js">
	<head>
		<title>WebAR2DTool</title>
		<meta charset='UTF-8'>
		<meta name='description' content='Web application for AR2DTool'>
		<meta id='metaLocale' name='locale' content='en-gb'>
		<meta content='width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
		<!--jQuery and jQuery libraries-->
		<script src='resources/js/jquery-latest.min.js' type='text/javascript'></script>
		<script src='resources/js/jquery-handler-toolkit.js' type='text/javascript'></script>
		<!--Modernizr library for detect browser functionalities-->
		<script src='resources/js/modernizr-custom.js' type='text/javascript'></script>
		<!--Page CSS and JavaScript-->
		<script src='resources/js/gui.js' type='text/javascript'></script>
		<script src='resources/js/configSpecialContainers.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/css/styles.css'>
		<link rel='stylesheet' href='resources/css/configSpecial.css'>
		<link rel='stylesheet' href='resources/css/animations.css'>
		<!--Zoom and panned library for Move and Zoom main image-->
		<script src='resources/js/zoomAndPanned.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/css/zoomAndPanned.css'>
		<!-- SweetAlert library (Beautifuls alerts, messages etc...-->
		<script src='resources/sweetAlert/sweetalert.min.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/sweetAlert/sweetalert.css'>
		<!-- Chosen and ImageSelect libraries (Selects for images in config panel)-->
		<script src='resources/imageSelect/chosen.jquery.min.js' type='text/javascript'></script>
		<script src='resources/imageSelect/ImageSelect.jquery.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/imageSelect/chosen.min.css'>
		<link rel='stylesheet' href='resources/imageSelect/Flat.css'>
		<link rel='stylesheet' href='resources/imageSelect/ImageSelect.css'>
	</head>
	<body>
		<div class='flex-container-column  maxMainAxisFlexSize maxCrossAxisFlexSize'>
			<header class='flex-container-row flex-item maxCrossAxisFlexSize header menu flex-shrink0'>
				<div class='flex-item Logo'><img src="resources/img/Logo_AR2DTool.png" alt="AR2DTool Logo" class='Logo LogoMin'></div>
				<nav class='navMenu flex-item-main maxCrossAxisFlexSize mainAxisFlexCenter crossAxisFlexCenter notActivateResponsive'>
					<div class="navMenuButton" onclick='showUploadPopUp()'><a>Upload a File</a></div>
					<div class="navMenuButton navDropDownContainer notActivateResponsive">
						<div class="flex-container-row crossAxisFlexCenter navDropDownClick">
							<a>Downloads</a>
							<img class="dropDownImage" src="resources/img/dropdown.png" alt="DropDown menu image">
							<img class="dropUpImage" src="resources/img/dropup.png" alt="DropUp menu image">
						</div>
						<div class='navDropDownItem'>
							<div><a href='webapi/methods/getGraphml' target="_blank">Download Graphml</a></div>
							<div><a href='webapi/methods/getDot' target="_blank">Download Dot</a></div>
							<div><a href='webapi/methods/getImage' target="_blank">Download Image</a></div>
						</div>
					</div>
					<div class="navMenuButton" onclick='alert("hola")'><a>About</a></div>
					<div class="navMenuButton" onclick='showReportPopup()'><a>Report a error</a></div>
				</nav>
				<div class='flex-item Logo'><img src="resources/img/OEG_Logo.png" alt="OEG Logo" class='Logo'></div>
				<div class='flex-item imgMenu'><img src="resources/img/nav.png" alt="Menu IMG" class='imgMenu'></div>
			</header>
			<section class='flex-container-row flex-item-main maxCrossAxisFlexSize maxMainAxisFlexSize mainContent'>
				<!-- for w3c validation -->
				<h2 class="displayNone">Dashborad</h2>
				<!-- end  w3c validation-->
				<aside id='dropMenuContainer' class='dropMenuContainerClose flex-item flex-container-row'>
					<div id='dropMenuContentContainer' class='dropMenuContentContainerClose dropMenuContentContainer'>
						<div id='configContainer' class="flex-container-column">
							<div id='keysContainer' data-configContainer='keys' class='flex-container-column padL5 padT5'>
								<div data-configParam='arrowColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowColor: </div>
									<div class='colorSelectBackground'>
										<select name='ColorSelect'>
											<option data-img-src='black' data-configValue="black" data-configSelected="true" class="selected" value="black" data-selected="">black</option>
											<option data-img-src='red' data-configValue="red" data-configSelected="true" class="selected" value="red">red</option>
											<option data-img-src='blue' data-configValue="blue" data-configSelected="true" class="selected" value="blue">blue</option>
											<option data-img-src='green' data-configValue="green" data-configSelected="true" class="selected" value="green">green</option>
											<option data-img-src='orange' data-configValue="orange" data-configSelected="true" class="selected" value="orange">orange</option>
											<option data-img-src='yellow' data-configValue="yellow" data-configSelected="true" class="selected" value="yellow">yellow</option>
										</select>
									</div>
								</div>
								<div data-configParam='literalColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>literalColor: </div>
									<div class='colorSelectBackground'>
										<select name='ColorSelect'>
											<option data-img-src='black' data-configValue="black" data-configSelected="true" class="selected" value="black" data-selected="">black</option>
											<option data-img-src='red' data-configValue="red" data-configSelected="true" class="selected" value="red">red</option>
											<option data-img-src='blue' data-configValue="blue" data-configSelected="true" class="selected" value="blue">blue</option>
											<option data-img-src='green' data-configValue="green" data-configSelected="true" class="selected" value="green">green</option>
											<option data-img-src='orange' data-configValue="orange" data-configSelected="true" class="selected" value="orange">orange</option>
											<option data-img-src='yellow' data-configValue="yellow" data-configSelected="true" class="selected" value="yellow">yellow</option>
										</select>
									</div>								
								</div>
								<div data-configParam='classColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>classColor: </div>
									<div class='colorSelectBackground'>
										<select name='ColorSelect'>
											<option data-img-src='black' data-configValue="black" data-configSelected="true" class="selected" value="black" data-selected="">black</option>
											<option data-img-src='red' data-configValue="red" data-configSelected="true" class="selected" value="red">red</option>
											<option data-img-src='blue' data-configValue="blue" data-configSelected="true" class="selected" value="blue">blue</option>
											<option data-img-src='green' data-configValue="green" data-configSelected="true" class="selected" value="green">green</option>
											<option data-img-src='orange' data-configValue="orange" data-configSelected="true" class="selected" value="orange">orange</option>
											<option data-img-src='yellow' data-configValue="yellow" data-configSelected="true" class="selected" value="yellow">yellow</option>
										</select>
									</div>								
								</div>
								<div data-configParam='individualColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>individualColor: </div>
									<div class='colorSelectBackground'>
										<select name='ColorSelect'>
											<option data-img-src='black' data-configValue="black" data-configSelected="true" class="selected" value="black" data-selected="">black</option>
											<option data-img-src='red' data-configValue="red" data-configSelected="true" class="selected" value="red">red</option>
											<option data-img-src='blue' data-configValue="blue" data-configSelected="true" class="selected" value="blue">blue</option>
											<option data-img-src='green' data-configValue="green" data-configSelected="true" class="selected" value="green">green</option>
											<option data-img-src='orange' data-configValue="orange" data-configSelected="true" class="selected" value="orange">orange</option>
											<option data-img-src='yellow' data-configValue="yellow" data-configSelected="true" class="selected" value="yellow">yellow</option>
										</select>
									</div>								
								</div>
								<div data-configParam='literalShape' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>literalShape: </div>
									<select>
										<option data-configValue='rectangle' data-configSelected=true class='selected' value='rectangle' selected>Rectangle</option>
										<option data-configValue='ellipse' data-configSelected=false class='notSelected' value='ellipse'>Ellipse</option>
										<option data-configValue='triangle' data-configSelected=false class='notSelected' value='triangle'>Triangle</option>
										<option data-configValue='diamond' data-configSelected=false class='notSelected' value='diamond'>Diamond</option>
									</select>							
								</div>
								<div data-configParam='individualShape' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>individualShape: </div>
									<select>
										<option data-configValue='rectangle' data-configSelected=true class='selected' value='rectangle' selected>Rectangle</option>
										<option data-configValue='ellipse' data-configSelected=false class='notSelected' value='ellipse'>Ellipse</option>
										<option data-configValue='triangle' data-configSelected=false class='notSelected' value='triangle'>Triangle</option>
										<option data-configValue='diamond' data-configSelected=false class='notSelected' value='diamond'>Diamond</option>
									</select>							
								</div>
								<div data-configParam='classShape' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>classShape: </div>
									<select>
										<option data-configValue='rectangle' data-configSelected=true class='selected' value='rectangle' selected>Rectangle</option>
										<option data-configValue='ellipse' data-configSelected=false class='notSelected' value='ellipse'>Ellipse</option>
										<option data-configValue='triangle' data-configSelected=false class='notSelected' value='triangle'>Triangle</option>
										<option data-configValue='diamond' data-configSelected=false class='notSelected' value='diamond'>Diamond</option>
									</select>							
								</div>
								<div data-configParam='nodeNameMode' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>nodeNameMode: </div>
									<select>
										<option data-configValue='localname' data-configSelected=true class='selected' value='localname' selected>Localname</option>
										<option data-configValue='fulluri' data-configSelected=false class='notSelected' value='fulluri'>Fulluri</option>
										<option data-configValue='prefix' data-configSelected=false class='notSelected' value='prefix'>Prefix</option>
									</select>							
								</div>
								<div data-configParam='arrowhead' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowhead: </div>
									<div class='imageSelectBackground'>
										<select name='ImageSelect'>
											<option data-img-src='resources/img/arrows/normal.gif' data-configValue='normal' data-configSelected=true class='selected' value='normal' selected>Normal</option>
											<option data-img-src='resources/img/arrows/onormal.gif' data-configValue='onormal' data-configSelected=true class='notSelected' value='onormal'>ONormal</option>
											<option data-img-src='resources/img/arrows/box.gif' data-configValue='box' data-configSelected=true class='notSelected' value='box'>Box</option>
											<option data-img-src='resources/img/arrows/obox.gif' data-configValue='obox' data-configSelected=true class='notSelected' value='obox'>OBox</option>
											<option data-img-src='resources/img/arrows/dot.gif' data-configValue='dot' data-configSelected=true class='notSelected' value='dot'>Dot</option>
											<option data-img-src='resources/img/arrows/odot.gif' data-configValue='odot' data-configSelected=true class='notSelected' value='odot'>ODot</option>
											<option data-img-src='resources/img/arrows/inv.gif' data-configValue='inv' data-configSelected=true class='notSelected' value='inv'>Inv</option>
											<option data-img-src='resources/img/arrows/oinv.gif' data-configValue='oinv' data-configSelected=true class='notSelected' value='oinv'>OInv</option>
											<option data-img-src='resources/img/arrows/diamond.gif' data-configValue='diamond' data-configSelected=true class='notSelected' value='diamond'>Diamond</option>
											<option data-img-src='resources/img/arrows/odiamond.gif' data-configValue='odiamond' data-configSelected=true class='notSelected' value='odiamond'>ODiamond</option>
										</select>
									</div>
								</div>
								<div data-configParam='arrowtail' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowtail: </div>
									<div class='imageSelectBackground'>
										<select name='ImageSelect'>
											<option data-img-src='resources/img/arrows/normal.gif' data-configValue='normal' data-configSelected=true class='selected' value='normal' selected>Normal</option>
											<option data-img-src='resources/img/arrows/onormal.gif' data-configValue='onormal' data-configSelected=true class='notSelected' value='onormal'>ONormal</option>
											<option data-img-src='resources/img/arrows/box.gif' data-configValue='box' data-configSelected=true class='notSelected' value='box'>Box</option>
											<option data-img-src='resources/img/arrows/obox.gif' data-configValue='obox' data-configSelected=true class='notSelected' value='obox'>OBox</option>
											<option data-img-src='resources/img/arrows/dot.gif' data-configValue='dot' data-configSelected=true class='notSelected' value='dot'>Dot</option>
											<option data-img-src='resources/img/arrows/odot.gif' data-configValue='odot' data-configSelected=true class='notSelected' value='odot'>ODot</option>
											<option data-img-src='resources/img/arrows/inv.gif' data-configValue='inv' data-configSelected=true class='notSelected' value='inv'>Inv</option>
											<option data-img-src='resources/img/arrows/oinv.gif' data-configValue='oinv' data-configSelected=true class='notSelected' value='oinv'>OInv</option>
											<option data-img-src='resources/img/arrows/diamond.gif' data-configValue='diamond' data-configSelected=true class='notSelected' value='diamond'>Diamond</option>
											<option data-img-src='resources/img/arrows/odiamond.gif' data-configValue='odiamond' data-configSelected=true class='notSelected' value='odiamond'>ODiamond</option>
										</select>
									</div>
								</div>
								<div data-configParam='arrowdir' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowdir: </div>
								<select>
									<option data-configValue='forward' data-configSelected=true class='selected' value='forward' selected>Forward</option>
									<option data-configValue='back' data-configSelected=false class='notSelected' value='back'>Back</option>
									<option data-configValue='both' data-configSelected=false class='notSelected' value='both'>Both</option>
									<option data-configValue='none' data-configSelected=false class='notSelected' value='none'>None</option>
								</select>
								</div>
								<div data-configParam='rankdir' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>rankdir: </div>
								<select>
									<option data-configValue='LR' data-configSelected=true class='selected' value='LR' selected>Left-Right</option>
									<option data-configValue='RL' data-configSelected=false class='notSelected' value='RL'>Right-Left</option>
									<option data-configValue='TB' data-configSelected=false class='notSelected' value='TB'>Top-Bottom</option>
									<option data-configValue='BT' data-configSelected=false class='notSelected' value='BT'>Bottom-Top</option>
								</select>
								</div>
								<div data-configParam='ignoreRdfType' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>ignoreRdfType: </div>
									<input data-configValue type="checkbox">
								</div>
								<div data-configParam='ignoreLiterals' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>ignoreLiterals: </div>
									<input data-configValue type="checkbox">
								</div>
								<div data-configParam='synthesizeObjectProperties' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>synthesizeObjectProperties: </div>
									<input data-configValue type="checkbox">
								</div>
							</div>
							<div data-configContainer='equivalentElementList' data-configParam='equivalentElementList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>equivalentElementList: </div>
								<div data-configSpecial='equivalentElementList' class="configListButton">Edit</div>
							</div>
							<div data-configContainer='ignoreElementList' data-configParam='ignoreElementList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>ignoreElementList: </div>
								<div data-configSpecial='ignoreElementList' class="configListButton">Edit</div>
							</div>
							<div data-configContainer='includeOnlyElementList' data-configParam='includeOnlyElementList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>includeOnlyElementList: </div>
								<div data-configSpecial='includeOnlyElementList' class="configListButton">Edit</div>
							</div>
							<div data-configContainer='specialElementsList' data-configParam='specialElementsList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>specialElementsList: </div>
								<div data-configSpecial='specialElementList' class="configListButton">Edit</div>
							</div>
						</div>
					</div>
					<div id='mobileLeftMenuEvents' class='flex-container-column mainAxisFlexCenter crossAxisFlexCenter dropClickMenu' onClick='changeStateLeftMenu();'>
						<div>
							<img id='droprightClickMenuImg' src="resources/img/dropright.png" alt="DropRight menu image" class='dropClickMenu' style='display:block'>
							<img id='dropleftClickMenuImg' src="resources/img/dropleft.png" alt="DropLeft menu image" class='dropClickMenu' style='display:none'>
						</div>
					</div>			
				</aside>
				<article id='includeOnlyElementList' class='maxWidthAndHeightAbsolute configSpecialPanel notAnimationOnLoad' style='display:none'>
					<!-- for w3c validation -->
					<h2 class="displayNone">Include only element list</h2>
					<!-- end  w3c validation-->
					<div class='flex-container-row flex-wrap maxWidthAndHeight'>
						<div id='includeOnlyElementListURIS' class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile configListContainer'>
						</div>
						<div class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile'>
							<p class='flex-align-center textTittleConfigSpecial'>Include Only List:</p>
							<div id='includeOnlyElementListAdded' class='flex-container-column flex-item-main maxCrossAxisFlexSize configListContainer'>							
							</div>
							<div class='configSpecialSubmitButton configListButton' onclick='toggleConfigSpecial("includeOnlyElementList")'>Save</div>
						</div>
					</div>
				</article>
				<article id='ignoreElementList' class='maxWidthAndHeightAbsolute configSpecialPanel notAnimationOnLoad' style='display:none'>
					<!-- for w3c validation -->
					<h2 class="displayNone">Ignore element list</h2>
					<!-- end  w3c validation-->
					<div class='flex-container-row flex-wrap maxWidthAndHeight'>
						<div id='ignoreElementListURIS' class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile configListContainer'>
						</div>
						<div class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile'>
							<p class='flex-align-center textTittleConfigSpecial'>Ignore Element List:</p>
							<div id='ignoreElementListAdded' class='flex-container-column flex-item-main maxCrossAxisFlexSize configListContainer'>							
							</div>
							<div class='configSpecialSubmitButton configListButton' onclick='toggleConfigSpecial("ignoreElementList")'>Save</div>
						</div>
					</div>
				</article>
				<article id='equivalentElementList' class='maxWidthAndHeightAbsolute configSpecialPanel notAnimationOnLoad' style='display:none'>
					<!-- for w3c validation -->
					<h2 class="displayNone">Equivalent element list</h2>
					<!-- end  w3c validation-->
					<div class='flex-container-row flex-wrap maxWidthAndHeight'>
						<div id='equivalentElementListURIS' class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile configListContainer'>
						</div>
						<div class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile'>
							<p class='flex-align-center textTittleConfigSpecial'>Equivalent List:</p>
							<div id='equivalentElementListAdded' class='flex-container-column flex-item-main maxCrossAxisFlexSize configListContainer'>						
							</div>
							<div class='configSpecialSubmitButton configListButton' onclick='toggleConfigSpecial("equivalentElementList")'>Save</div>
						</div>
					</div>
				</article>
				<article id='specialElementList' class='maxWidthAndHeightAbsolute configSpecialPanel notAnimationOnLoad' style='display:none'>
					<!-- for w3c validation -->
					<h2 class="displayNone">Special element list</h2>
					<!-- end  w3c validation-->
					<div class='flex-container-row flex-wrap maxWidthAndHeight'>
						<div id='specialElementListURIS' class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile configListContainer'>
						</div>
						<div class='flex-container-column flex-item-main maxCrossAxisFlexSize configSpecialMinMaxMobile'>
							<p class='flex-align-center textTittleConfigSpecial'>Edit special element:</p>
							<div id='specialElementListEdit' class='flex-container-column flex-item-main maxCrossAxisFlexSize specialListEditContainer'>
								<div class='specialListParamContainer flex-container-row crossAxisFlexCenter'><div class='specialListKey'>Edit: </div>
									<div data-specialList='edit' class='specialCheckbox'></div>
								</div>			
								<div  data-specialList='shapeContainer' class='specialListParamContainer flex-container-row crossAxisFlexCenter disableSpecial'><div class='specialListKey'>Shape: </div>
									<select  data-specialList='shape'>
										<option data-specialList='rectangle' class='selected' value='rectangle' selected>Rectangle</option>
										<option data-specialList='ellipse' class='notSelected' value='ellipse'>Ellipse</option>
										<option data-specialList='triangle' class='notSelected' value='triangle'>Triangle</option>
										<option data-specialList='diamond' class='notSelected' value='diamond'>Diamond</option>
									</select>							
								</div>
								<div  data-specialList='colorContainer' class='specialListParamContainer flex-container-row crossAxisFlexCenter disableSpecial'><div class='specialListKey'>Color: </div>
									<div id='toResetSpecialColor' class='colorSelectBackground'>
										<select name='SelectSpecialColor'  data-specialList='color'>
											<option data-img-src='black' data-specialList="black" class="selected" value="black" data-selected="">black</option>
											<option data-img-src='red' data-specialList="red"  class="notSelected" value="red">red</option>
											<option data-img-src='blue' data-specialList="blue" class="notSelected" value="blue">blue</option>
											<option data-img-src='green' data-specialList="green" class="notSelected" value="green">green</option>
											<option data-img-src='orange' data-specialList="orange" class="notSelected" value="orange">orange</option>
											<option data-img-src='yellow' data-specialList="yellow" class="notSelected" value="yellow">yellow</option>
										</select>
									</div>
								</div>
							</div>
							<div class='configSpecialSubmitButton configListButton' onclick='toggleConfigSpecial("specialElementList")'>Save</div>
						</div>
					</div>
				</article>
				<article class='flex-container-column flex-item-main maxCrossAxisFlexSize maxMainAxisFlexSize imageContainer'>
					<!-- for w3c validation -->
					<h2 class="displayNone">Image container</h2>
					<!-- end  w3c validation-->
					<div id='imageContainerZoomAndPan' class='flex-item-main maxCrossAxisFlexSize maxMainAxisFlexSize'></div>
				</article>
			</section>
		</div>
		<div class='generateDiv' onclick='generateImage()'><img src='resources/img/generateIcon.png'></img></div>
	</body>
</html>

 
