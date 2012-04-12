<?php 

$simulation = $_GET['simtitle'];

if($simulation == ''){
	include("data/simulations/list.php"); 
} else {
	switch($simulation){
			case 'leimen':
				include("data/simulations/Leimen.php"); 
				break;
			case 'glasgow':
				include("data/simulations/Glasgow.php"); 
				break;
			case 'testland':
				include("data/simulations/Testland.php"); 
				break;
			case 'valencia':
				include("data/simulations/Valencia.php"); 
				break;
			case 'office':
				include("data/simulations/Office.php"); 
				break;
			default:
				$notfound = true;
   				include("data/misc/404.php");
				break;
		}
	if(!$notfound) {
		//Draws the sim descripton using the variables set in the switch above
		include("data/simulations/content.php");
	}
}
?>
