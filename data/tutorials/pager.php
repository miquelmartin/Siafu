	<div id="pager"> 
            	<?
				if( $step > $first){
					?><a href="?what=tutorial&step=<? echo $step-1; ?>">&laquo;prev</a> &bull; <?
				}

				for($i=$first; $i <= $last; $i++){
					if($i==$step) {
						echo " ".$i." ";
					}
					else{
						?> <a href="?what=tutorial&step=<? echo $i; ?>"><? echo $i; ?></a> <?			
					}
				}
				if( $step < $last){
					?> &bull; <a href="?what=tutorial&step=<? echo $step+1; ?>">next&raquo;</a><?
				}
				?>
	</div>