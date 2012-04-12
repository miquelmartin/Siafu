<?php
$name = array("Leimen", "Glasgow", "TestLand" , "Valencia", "Office");
$link = array("leimen","glasgow","testland","valencia", "office");
$image = array("LeimenPreview.png", "GlasgowPreview.png", "TestLandPreview.png", "ValenciaPreview.png", "OfficePreview.png");
?>

<div style="text-align: center">
<table id="simulationlist" class="center">
	<tr>
<?
for($i=0; $i < count($name); $i++){
	if($i!= 0 && $i%2 == 0) { echo "</tr><tr>"; }
	$size = getimagesize("data/simulations/screenshots/preview/".$image[$i]);
	?>
    <td  style="background:url(data/simulations/screenshots/preview/<? echo $image[$i] ?>) no-repeat; " >

    	<a href="?what=simulations&simtitle=<? echo $link[$i] ?>">
			<img src="images/transparent.gif" width="<? echo $size[0]+10; ?>" height="65" /><? echo $name[$i] ?>
    	</a>
	</td>
    <?
}
?>
</tr>
</table>
</div>
<p>If you would like to create your own simulation, check the <a href="http://siafusimulator.sourceforge.net/?what=tutorial">tutorial</a> and the <a href="http://siafusimulator.sourceforge.net/?what=developers">developer documents</a>. Also <a href="http://siafusimulator.sourceforge.net/?what=contact">tell us</a> about it if you'd like to display it here!