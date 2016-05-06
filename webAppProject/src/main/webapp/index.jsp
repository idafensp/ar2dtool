<!DOCTYPE html>
<html lang='<%=request.getLocale()%>' class="no-js">
	<head>
		<title>WebAR2DTool</title>
		<meta charset='UTF-8'>
		<meta name='description' content='Web application for AR2DTool'>
		<meta id='metaLocale' name='locale' content='<%=request.getLocale()%>'>
		<meta content='width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
		<!--jQuery and jQuery libraries-->
		<script src='http://code.jquery.com/jquery-latest.min.js' type='text/javascript'></script>
		<script src='resources/js/jquery-handler-toolkit.js' type='text/javascript'></script>
		<!--Modernizr library for detect browser functionalities-->
		<script src='resources/js/modernizr-custom.js' type='text/javascript'></script>
		<!--Page CSS and JavaScript-->
		<script src='resources/js/gui.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/css/styles.css'>
		<!--Zoom and panned library-->
		<script src='resources/js/zoomAndPanned.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/css/zoomAndPanned.css'>
		<!-- SweetAlert library -->
		<script src='resources/sweetAlert/sweetalert.min.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/sweetAlert/sweetalert.css'>
	</head>
	<body>
		<div class='flex-container-column  maxMainAxisFlexSize maxCrossAxisFlexSize'>
			<header class='flex-container-row flex-item maxCrossAxisFlexSize header menu flex-shrink0'>
				<div class='flex-item Logo'><img src="resources/img/Logo_AR2DTool.png" alt="AR2DTool Logo" class='Logo'></div>
				<nav class='navMenu flex-item-main maxCrossAxisFlexSize mainAxisFlexCenter crossAxisFlexCenter'>
				<div><a onclick='showUploadPopUp()'>Upload a File</a></div>
				<div><a href='webapi/methods/getGraphml' target="_blank">Download Graphml</a></div>
				<div><a href='webapi/methods/getDot' target="_blank">Download Dot</a></div>
				<div><a href='webapi/methods/getImage' target="_blank">Download Image</a></div>
				<div><a href='webapi/methods/getAR2DToolLog' target="_blank">Download AR2DTool Log</a></div>
				</nav>
				<div class='flex-item Logo'><img src="resources/img/OEG_Logo.png" alt="OEG Logo" class='Logo'></div>
			</header>
			<section class='flex-container-row flex-item-main maxCrossAxisFlexSize maxMainAxisFlexSize mainContent'>
				<aside id='dropMenuContainer' class='dropMenuContainerClose flex-item flex-container-row'>
					<div id='dropMenuContentContainer' class='dropMenuContentContainerClose dropMenuContentContainer'>
						<div id='configContainer' class="flex-container-column">
							<div id='keysContainer' configContainer='keys' class='flex-container-column padL5 padT5'>
								<div configParam='arrowColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowColor: </div>
									<select>
										<option style="background-color:black;color:black;font-size:140%" configvalue="black" configselected="true" class="selected" value="black" selected="">black</option>
										<option style="background-color:red;color:red;font-size:140%" configvalue="red" configselected="true" class="selected" value="red">red</option>
										<option style="background-color:blue;color:blue;font-size:140%" configvalue="blue" configselected="true" class="selected" value="blue">blue</option>
										<option style="background-color:green;color:green;font-size:140%" configvalue="green" configselected="true" class="selected" value="green">green</option>
										<option style="background-color:orange;color:orange;font-size:140%" configvalue="orange" configselected="true" class="selected" value="orange">orange</option>
										<option style="background-color:yellow;color:yellow;font-size:140%" configvalue="yellow" configselected="true" class="selected" value="yellow">yellow</option>
									</select>
								</div>
								<div configParam='literalColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>literalColor: </div>
									<select>
										<option style="background-color:black;color:black;font-size:140%" configvalue="black" configselected="true" class="selected" value="black" selected="">black</option>
										<option style="background-color:red;color:red;font-size:140%" configvalue="red" configselected="true" class="selected" value="red">red</option>
										<option style="background-color:blue;color:blue;font-size:140%" configvalue="blue" configselected="true" class="selected" value="blue">blue</option>
										<option style="background-color:green;color:green;font-size:140%" configvalue="green" configselected="true" class="selected" value="green">green</option>
										<option style="background-color:orange;color:orange;font-size:140%" configvalue="orange" configselected="true" class="selected" value="orange">orange</option>
										<option style="background-color:yellow;color:yellow;font-size:140%" configvalue="yellow" configselected="true" class="selected" value="yellow">yellow</option>
									</select>								
								</div>
								<div configParam='classColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>classColor: </div>
									<select>
										<option style="background-color:black;color:black;font-size:140%" configvalue="black" configselected="true" class="selected" value="black" selected="">black</option>
										<option style="background-color:red;color:red;font-size:140%" configvalue="red" configselected="true" class="selected" value="red">red</option>
										<option style="background-color:blue;color:blue;font-size:140%" configvalue="blue" configselected="true" class="selected" value="blue">blue</option>
										<option style="background-color:green;color:green;font-size:140%" configvalue="green" configselected="true" class="selected" value="green">green</option>
										<option style="background-color:orange;color:orange;font-size:140%" configvalue="orange" configselected="true" class="selected" value="orange">orange</option>
										<option style="background-color:yellow;color:yellow;font-size:140%" configvalue="yellow" configselected="true" class="selected" value="yellow">yellow</option>
									</select>								
								</div>
								<div configParam='individualColor' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>individualColor: </div>
									<select>
										<option style="background-color:black;color:black;font-size:140%" configvalue="black" configselected="true" class="selected" value="black" selected="">black</option>
										<option style="background-color:red;color:red;font-size:140%" configvalue="red" configselected="true" class="selected" value="red">red</option>
										<option style="background-color:blue;color:blue;font-size:140%" configvalue="blue" configselected="true" class="selected" value="blue">blue</option>
										<option style="background-color:green;color:green;font-size:140%" configvalue="green" configselected="true" class="selected" value="green">green</option>
										<option style="background-color:orange;color:orange;font-size:140%" configvalue="orange" configselected="true" class="selected" value="orange">orange</option>
										<option style="background-color:yellow;color:yellow;font-size:140%" configvalue="yellow" configselected="true" class="selected" value="yellow">yellow</option>
									</select>								
								</div>
								<div configParam='literalShape' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>literalShape: </div>
									<select>
										<option configValue='rectangle' configSelected=true class='selected' value='rectangle' selected>Rectangle</option>
										<option configValue='ellipse' configSelected=false class='notSelected' value='ellipse'>Ellipse</option>
										<option configValue='triangle' configSelected=false class='notSelected' value='triangle'>Triangle</option>
										<option configValue='diamond' configSelected=false class='notSelected' value='diamond'>Diamond</option>
									</select>							
								</div>
								<div configParam='individualShape' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>individualShape: </div>
									<select>
										<option configValue='rectangle' configSelected=true class='selected' value='rectangle' selected>Rectangle</option>
										<option configValue='ellipse' configSelected=false class='notSelected' value='ellipse'>Ellipse</option>
										<option configValue='triangle' configSelected=false class='notSelected' value='triangle'>Triangle</option>
										<option configValue='diamond' configSelected=false class='notSelected' value='diamond'>Diamond</option>
									</select>							
								</div>
								<div configParam='classShape' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>classShape: </div>
									<select>
										<option configValue='rectangle' configSelected=true class='selected' value='rectangle' selected>Rectangle</option>
										<option configValue='ellipse' configSelected=false class='notSelected' value='ellipse'>Ellipse</option>
										<option configValue='triangle' configSelected=false class='notSelected' value='triangle'>Triangle</option>
										<option configValue='diamond' configSelected=false class='notSelected' value='diamond'>Diamond</option>
									</select>							
								</div>
								<div configParam='nodeNameMode' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>nodeNameMode: </div>
									<select>
										<option configValue='localname' configSelected=true class='selected' value='localname' selected>Localname</option>
										<option configValue='fulluri' configSelected=false class='notSelected' value='fulluri'>Fulluri</option>
										<option configValue='prefix' configSelected=false class='notSelected' value='prefix'>Prefix</option>
									</select>							
								</div>
								<div configParam='arrowhead' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowhead: </div>
								<select>
									<option configValue='normal' configSelected=true class='selected' value='normal' selected>Normal</option>
									<option configValue='inv' configSelected=false class='notSelected' value='inv'>Inv</option>
									<option configValue='dot' configSelected=false class='notSelected' value='dot'>Dot</option>
									<option configValue='invdot' configSelected=false class='notSelected' value='invdot'>Invdot</option>
									<option configValue='odot' configSelected=false class='notSelected' value='odot'>Odot</option>
									<option configValue='invodot' configSelected=false class='notSelected' value='invodot'>Invodot</option>
									<option configValue='none' configSelected=false class='notSelected' value='none'>None</option>
									<option configValue='tee' configSelected=false class='notSelected' value='tee'>Tee</option>
									<option configValue='empty' configSelected=false class='notSelected' value='empty'>Empty</option>
									<option configValue='invempty' configSelected=false class='notSelected' value='invempty'>Invempty</option>
									<option configValue='diamond' configSelected=false class='notSelected' value='diamond'>Diamond</option>
									<option configValue='odiamond' configSelected=false class='notSelected' value='odiamond'>Odiamond</option>
									<option configValue='ediamond' configSelected=false class='notSelected' value='ediamond'>Ediamond</option>
									<option configValue='crow' configSelected=false class='notSelected' value='crow'>Crow</option>
									<option configValue='box' configSelected=false class='notSelected' value='box'>Box</option>
									<option configValue='obox' configSelected=false class='notSelected' value='obox'>Obox</option>
									<option configValue='open' configSelected=false class='notSelected' value='open'>Open</option>
									<option configValue='halfopen' configSelected=false class='notSelected' value='halfopen'>Halfopen</option>
									<option configValue='vee' configSelected=false class='notSelected' value='vee'>Vee</option>
									<option configValue='circle' configSelected=false class='notSelected' value='circle'>Circle</option>
								</select>
								</div>
								<div configParam='arrowtail' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowtail: </div>
								<select>
									<option configValue='normal' configSelected=true class='selected' value='normal' selected>Normal</option>
									<option configValue='inv' configSelected=false class='notSelected' value='inv'>Inv</option>
									<option configValue='dot' configSelected=false class='notSelected' value='dot'>Dot</option>
									<option configValue='invdot' configSelected=false class='notSelected' value='invdot'>Invdot</option>
									<option configValue='odot' configSelected=false class='notSelected' value='odot'>Odot</option>
									<option configValue='invodot' configSelected=false class='notSelected' value='invodot'>Invodot</option>
									<option configValue='none' configSelected=false class='notSelected' value='none'>None</option>
									<option configValue='tee' configSelected=false class='notSelected' value='tee'>Tee</option>
									<option configValue='empty' configSelected=false class='notSelected' value='empty'>Empty</option>
									<option configValue='invempty' configSelected=false class='notSelected' value='invempty'>Invempty</option>
									<option configValue='diamond' configSelected=false class='notSelected' value='diamond'>Diamond</option>
									<option configValue='odiamond' configSelected=false class='notSelected' value='odiamond'>Odiamond</option>
									<option configValue='ediamond' configSelected=false class='notSelected' value='ediamond'>Ediamond</option>
									<option configValue='crow' configSelected=false class='notSelected' value='crow'>Crow</option>
									<option configValue='box' configSelected=false class='notSelected' value='box'>Box</option>
									<option configValue='obox' configSelected=false class='notSelected' value='obox'>Obox</option>
									<option configValue='open' configSelected=false class='notSelected' value='open'>Open</option>
									<option configValue='halfopen' configSelected=false class='notSelected' value='halfopen'>Halfopen</option>
									<option configValue='vee' configSelected=false class='notSelected' value='vee'>Vee</option>
									<option configValue='circle' configSelected=false class='notSelected' value='circle'>Circle</option>
								</select>
								</div>
								<div configParam='arrowdir' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>arrowdir: </div>
								<select>
									<option configValue='forward' configSelected=true class='selected' value='forward' selected>Forward</option>
									<option configValue='back' configSelected=false class='notSelected' value='back'>Back</option>
									<option configValue='both' configSelected=false class='notSelected' value='both'>Both</option>
									<option configValue='none' configSelected=false class='notSelected' value='none'>None</option>
								</select>
								</div>
								<div configParam='rankdir' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>rankdir: </div>
								<select>
									<option configValue='LR' configSelected=true class='selected' value='LR' selected>Left-Right</option>
									<option configValue='RL' configSelected=false class='notSelected' value='RL'>Right-Left</option>
									<option configValue='TB' configSelected=false class='notSelected' value='TB'>Top-Bottom</option>
									<option configValue='BT' configSelected=false class='notSelected' value='BT'>Bottom-Top</option>
								</select>
								</div>
								<div configParam='ignoreRdfType' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>ignoreRdfType: </div>
									<input configValue type="checkbox">
								</div>
								<div configParam='ignoreLiterals' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>ignoreLiterals: </div>
									<input configValue type="checkbox">
								</div>
								<div configParam='synthesizeObjectProperties' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>synthesizeObjectProperties: </div>
									<input configValue type="checkbox">
								</div>
							</div>
							<div configContainer='equivalentElementList' configParam='equivalentElementList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>equivalentElementList: </div>
								<input configValue type="text">
							</div>
							<div configContainer='ignoreElementList' configParam='ignoreElementList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>ignoreElementList: </div>
								<input configValue type="text">
							</div>
							<div configContainer='includeOnlyElementList' configParam='includeOnlyElementList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>includeOnlyElementList: </div>
								<input configValue type="text">
							</div>
							<div configContainer='specialElementList' configParam='specialElementsList' class='configKeyParamContainer flex-container-row crossAxisFlexCenter'><div>specialElementsList: </div>
								<input configValue type="text">
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
				<article id='imageContainerZoomAndPan' class='flex-item-main maxCrossAxisFlexSize maxMainAxisFlexSize imageContainer'>
				</article>
			</section>
		</div>
		<div class='generateDiv' ><button class='generateButton' onclick='generateImage()'>Generate</button></div>
	</body>
</html>

 
