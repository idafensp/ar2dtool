<!DOCTYPE html>
<html lang='<%=request.getLocale()%>'>
	<head>
		<title>WebAR2DTool</title>
		<meta charset='UTF-8'>
		<meta name='description' content='Web application for AR2DTool'>
		<meta id='metaLocale' name='locale' content='<%=request.getLocale()%>'>
		<meta content='width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
		<!--jQuery and jQuery mobile-->
		<script src='http://code.jquery.com/jquery-latest.min.js' type='text/javascript'></script>
		<!--Page CSS and JavaScript-->
		<script src='resources/js/gui.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/css/styles.css'>
		<!--Zoom and panned library-->
		<script src='resources/js/zoomAndPanned.js' type='text/javascript'></script>
		<link rel='stylesheet' href='resources/css/zoomAndPanned.css'>
	</head>
	<body>
		<div class='flex-container-column  maxMainAxisFlexSize maxCrossAxisFlexSize'>
			<div class='flex-container-row flex-item maxCrossAxisFlexSize header menu flex-shrink0'>
				<div class='flex-item Logo'><img src="resources/img/Logo_AR2DTool.png" alt="AR2DTool Logo" class='Logo'></div>
				<div class='navMenu flex-item-main maxCrossAxisFlexSize flex-container-row mainAxisFlexCenter crossAxisFlexCenter'>
					<a>MI MENU</a>
				</div>
				<div class='flex-item Logo'><img src="resources/img/OEG_Logo.png" alt="OEG Logo" class='Logo'></div>
			</div>
			<div class='flex-container-row flex-item-main maxCrossAxisFlexSize maxMainAxisFlexSize mainContent'>
				<div id='dropMenuContainer' class='dropMenuContainerClose flex-item flex-container-row'>
					<div id='dropMenuContentContainer' class='dropMenuContentContainerClose dropMenuContentContainer'>
						<p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p><p>afhguhruoghwuohguowheuoge</p>
					</div>
					<div id='mobileLeftMenuEvents' class='flex-container-column mainAxisFlexCenter crossAxisFlexCenter dropClickMenu' onClick='changeStateLeftMenu();'>
						<div>
							<img id='droprightClickMenuImg' src="resources/img/dropright.png" alt="DropRight menu image" class='dropClickMenu' style='display:block'>
							<img id='dropleftClickMenuImg' src="resources/img/dropleft.png" alt="DropLeft menu image" class='dropClickMenu' style='display:none'>
						</div>
					</div>			
				</div>
				<div id='imageContainerZoomAndPan' class='flex-item-main maxCrossAxisFlexSize maxMainAxisFlexSize imageContainer'>
				</div>
			</div>
		</div>
		<div class='generateDiv' ><button class='generateButton' onclick='generateImage()'>Generar</button></div>
	</body>
</html>

 
