<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
Design by Free CSS Templates
http://www.freecsstemplates.org
Released for free under a Creative Commons Attribution 2.5 License
-->
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Siafu: an Open Source Context Simulator</title>
<meta name="keywords" content="context simulator siafu open source free simulation" />
<meta name="description" content="Siafu project page" />
<link href="default.css" rel="stylesheet" type="text/css" />
<!-- <script type="text/javascript" src="scripts/mootools.v1.11.js"></script> -->
</head>
<body>
<div id="header">
  <div id="logo">
    <h1>Siafu</h1>
    <h2>An Open Source<br/>Context Simulator</h2>
  </div>
   <div id="logoright">&nbsp;</div>
  <div id="menu">
    <ul>
      <li class="first"><a href="?what=home" accesskey="1" title="">Home</a></li>
      <li><a href="?what=simulations">Simulations</a></li>
      <li><a href="?what=developers">Developers</a></li>
      <li><a href="?what=faq">FAQ</a></li>
      <li><a href="?what=download">Download</a></li>
      <li><a href="?what=contact">Contact</a></li>
    </ul>

  </div>
</div>
<div id="content">
  <div id="colOne">
    <?php 
	
	$what = $_GET['what'];
	
	switch($what){
	case '':
	case 'home':
		include("data/home/Intro.php"); 
		break;

	case 'simulations':
		include("data/simulations/simulations.php");
		break;

	case 'tutorial':
		include("data/tutorials/tutorial.php"); 
		break;	

	case 'faq':
		include("data/FAQ/faq.php"); 
		break;	

	case 'developers':
		include("data/developers/developers.php"); 
		break;	
		
	case 'contact':
		include("data/Contact/Contact.php"); 
		break;	
		
	case 'download':
		include("data/download/download.php"); 
		break;	
				
	case 'search':
		include("data/misc/Search.php");
		break;
		
	default:
   		include("data/misc/404.php");
		break;
	}

	?>
  </div>
  <div id="colTwo">

    <?php
	include("menuitems/sourceforge.php");
	include("menuitems/searchbox.php");
	
	switch($what){
		case '':
		case 'home':
			include("menuitems/simulations.php");
			break;

		default:
			break;
	}
	
	?>
  </div>
  <div style="clear: both;">&nbsp;</div>
</div>
<div id="footer">
  <p>(c) 2007 NEC Europe Ltd. Based on a css template from <a href="http://www.freecsstemplates.org/">Free
      CSS Templates</a>. <a href=
"http://validator.w3.org/check/referer">XHTML</a> | <a href=
"http://jigsaw.w3.org/css-validator/check/referer">CSS</a></p>
</div>
<?php include("scripts/googleanalytics.php"); ?>
</body>
</html>
