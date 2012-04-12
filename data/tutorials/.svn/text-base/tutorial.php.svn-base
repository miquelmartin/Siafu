<?php
$first=1;
$last=4;

$step = $_GET['step'];

if($step == ''){ 
	$step=1; 
} 

if($step == "all"){
	for($step=1; $step < 5; $step++){
		include("data/tutorials/".$step.".php");
	}
} else if(!is_numeric($step) || $step > $last || $step < $first){
		include("data/misc/404.php");
} else {
		include("data/tutorials/pager.php");
		include("data/tutorials/".$step.".php");
		
		?><p>&nbsp;</p><p style="text-align:left;"><a href="?what=tutorial&step=all">Printer friendly version</a></p><?
		include("data/tutorials/pager.php");
}
?>

