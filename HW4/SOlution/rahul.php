<?php
	// make sure browsers see this page as utf-8 encoded HTML
	header('Content-Type: text/html; charset=utf-8');
	$limit = 10;
	$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
	$results = false;
	
	if ($query)
	{
		// The Apache Solr Client library should be on the include path
		// which is usually most easily accomplished by placing in the
		// same directory as this script ( . or current directory is a default
		// php include path entry in the php.ini)
		require_once('solr-php-client/Apache/Solr/Service.php');
		
		// create a new solr service instance - host, port, and corename
		// path (all defaults in this example)
		$solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/');
		
		// if magic quotes is enabled then stripslashes will be needed
		if (get_magic_quotes_gpc() == 1)
		{
			$query = stripslashes($query);
		}

		$pagerank_set = array();

		// in production code you'll always want to use a try /catch for any
		// possible exceptions emitted by searching (i.e. connection
		// problems or a query parsing error)
		try
		{
			if (array_key_exists("algorithm", $_REQUEST) && $_REQUEST["algorithm"]=="pagerank" ) 
			{
				$pagerank_set['sort'] ="pageRankFile desc";
				$results = $solr->search($query, 0, $limit, $pagerank_set);
			}
			
			else
				$results = $solr->search($query, 0, $limit);	
		}

		catch (Exception $e)
		{
			// in production you'd probably log or email this error to an admin
			// and then show a special message to the user but for this example
			// we're going to show the full exception
			die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
		}
	}
?>

<html>
	<head>
		<title>PHP Solr Client Example</title>
	</head>
	<body>
			<h2>CSCI 572      : Information Retrieval and Web Seach Engines</h2>
			<h4>Homework #4 : Comparing Search Engine Ranking Algorithms </h4>


			<form accept-charset="utf-8" method="get">
				<label for="q">Search :</label>
				<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/><br/> 
				
				<input type="radio" name="algorithm" value="lucene" checked="checked"  /> Lucene <br/>
				
				<input type="radio" name="algorithm" value="pagerank" <?php if($_REQUEST['algorithm']=='pageRank' ) echo "checked"; ?> /> PageRank algorithm <br/><br/> 

				<input type="submit"/>
			</form>

		<?php
			// display results
			if ($results)
			{
				$total = (int) $results->response->numFound;
				$start = min(1, $total);
				$end = min($limit, $total);
		?>
				<div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
				<ol>
			<?php
					// iterate result documents
					foreach ($results->response->docs as $doc)
					{			$ID="N/A";
								$Title="N/A";
								$Desc="N/A";
								$URL="N/A";
			?>
					<li>
						<table style=" text-align: left">
				<?php
		
							// iterate document fields / values : JSON formatted values
							foreach ($doc as $field => $value)
							{
								if($field== "id" )
								{
									$ID=$value;
								}
								if($field == "title")
								{
									if ( sizeof($value) >1)									
										$Title=$value[0];
									else
										$Title=$value;
								}
								if($field == "og_description")
								{
									$Desc=$value;
								}
								if($field == "og_url")
								{
									//$URL=$value;
									if ( sizeof($value) >1)									
										$URL=$value[0];
									else
										$URL=$value;
								}
				
							/*
							<tr>
						 	<th><?php echo htmlspecialchars($field, ENT_NOQUOTES, 'utf-8'); ?></th>
							<td><?php echo htmlspecialchars($value, ENT_NOQUOTES, 'utf-8'); ?></td> 

							</tr>
							*/
				?>
				<?php
							}


							$ID2=str_replace("/home/rahul/Desktop/HW4-Rahul/Data/latimes-20181112T035524Z-001/latimes/latimes/latimes/","",$ID);
							if($URL=="N/A")
							{
								
								try
								{
									$file1 = fopen("URLtoHTML_latimes.csv","r");
									
									while($line = fgetcsv($file1,0,","))
									{
										if($ID2 == $line[0])
										{
											$URL = $line[1];
										}
										
									}
									fclose($file1);
								}
								catch (Exception $e)
								{     //if ID is null or empty, do a silent pass
								}
							}

							//Displaying ID with full path according to Piazza post @404							
							echo "<tr>   
							Title                                            : <a href='$URL' target='_blank'>$Title</a> </br>
							URL                          			 : <a href='$URL' target='_blank'>$URL</a> </br>
							ID                        		         : $ID </br>   
							Description : $Desc
							</tr>";

				?>
						</table>
					</li>
				<?php
					}
				?>
				</ol>
			<?php
			}
			?>
	</body>
</html>
