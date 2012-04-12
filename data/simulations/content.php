<table class="simulation">
  <tr>
    <td class="simpreview"><a href="data/simulations/screenshots/full/<? echo $mainscreenshotFull; ?>" ><img src="data/simulations/screenshots/preview/<? echo $mainscreenshot; ?>"/></a></td>
    <td class="simdescription">
      <h1><? echo $name; ?></h1>
      <ul>
        <li><span>Type of scenario:</span> <? echo $scenariotype; ?></li>
        <li><span>Agents:</span> <? echo $agents; ?></li>
        <li><span>Highlights:</span> <? echo $highlight; ?></li>
        <li><span>License:</span> <? echo $license; ?></li>
        <li><span>Website:</span> <a class="external" href="<? echo $websitelinks; ?>"> <? echo $website; ?></a></li>
        <li><span>Download link:</span> <a class="external" href="<? echo $downloadlink; ?>">here</a></li>
      </ul>
      <? echo $description; ?> </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <table width="100%">
        <tr>
          <td style="vertical-align:top;">
            <p style="font-weight:bold">Further screenshots:</p>
          </td>
          <td width="100%">
          	<?
			for($i=count($screenshots)-1; $i >= 0; $i--){
				?>
                <a href="data/simulations/screenshots/full/<? echo $screenshotsFull[$i] ?>" >
                <img class="screenshot" src="data/simulations/screenshots/preview/<? echo $screenshots[$i] ?> "/>
                </a>
                <?
			}
	          ?>

            <p style="clear:both; text-align:right"> Click on thescreenshots
              to enlarge. </p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td> </td>
  </tr>
</table>