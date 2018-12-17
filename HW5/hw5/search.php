<?php
	include 'simple_html_dom.php';
	include 'SpellCorrector.php';

	ini_set('memory_limit','-1');                         // This value has been set according to TA note in piazza @492
	
	header('Content-Type: text/html; charset=utf-8');
	
	$SERP = false;
	$output = "";
	$limit = 10;

	$flag=false;
	$correct1 = "";

	$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
	$correct2="";
	

	if ($query)
	{
		
		require_once('solr-php-client/Apache/Solr/Service.php');
		
		$solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/');

		if (get_magic_quotes_gpc() == 1)
		{
		  	$query = stripslashes($query);
		}
	 
		$choice = isset($_GET['algorithm']) && $_GET['algorithm'] == "pagerank" ? "pagerank" : "default";
	 
		try
		{
			if($choice == "default")
			{ 	
				$additionalParameters=array('sort' => '');
			}
			else
			{
			   	$additionalParameters=array('sort' => 'pageRankFile desc');    //PageRank
			}
			
			$word = explode(" ",$query);				// PHP .split()
			   
	 	        for($i=0;$i<sizeOf($word);$i++)
			{
			      ini_set('memory_limit',-1);
			      ini_set('max_execution_time', 300);
			      $corrected = SpellCorrector::correct($word[$i]);
			      
			      if($correct1!="")
			      {	
			      	$correct1 = $correct1."+".trim($corrected);    // PHP string concatenation
			      }
			      else
			      {
				$correct1 = trim($corrected);
			      }
				$correct2 = $correct2." ".trim($corrected);
			}
			    
			$correct2 = str_replace("+"," ",$correct1);

			$flag=false;
						
			if(strtolower($query)==strtolower($correct2))
			{
				$SERP = $solr->search($query, 0, $limit, $additionalParameters);
			}
			else 
			{

			      $output = "Did you mean: <a href='$link'>$correct2</a>";
			      $flag =true;

			      $link = "http://localhost:8080/ranking.php?q=$correct1&sort=$choice";
			      $SERP = $solr->search($query, 0, $limit, $additionalParameters);
			      
			}
				
		}
	  catch (Exception $e)
	  {
	    	die();
	  }
	}
?>

<html>
	<head>
    	    <title>Enhanced Solr Search Engine</title>
	    <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	    <link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Ubuntu" />
	    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
	    <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
	    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	    <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	</head>
	
	<body>
		<h2 align="center">CSCI 572      : Information Retrieval and Web Seach Engines</h2>
		<h2 align="center"> Spell Checking, AutoComplete and Snippets </h2>
		<h1 align="center" > <font font-family: Ubuntu;  font-size: 48px; >
		
			<font color="blue">S</font> 
			<font color="red">o</font> 
			<font color="yellow">l</font> 
			<font color="blue">r </font>
			
			&nbsp;&nbsp;
		
			<font color="green">  S</font> 
			<font color="red">e</font> 
			<font color="blue">a</font> 
			<font color="red">r</font> 
			<font color="yellow">c</font> 
			<font color="blue">h</font>  </font>

		</h1><br/>
		     
		<form accept-charset="utf-8" method="get" align="center">

			<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" list="SERP"  placeholder="Enter what you want to search" autocomplete="off"/>
			<br>
			<datalist id="SERP"></datalist>
			<input checked type="radio" name="algorithm" <?php if (isset($_GET['algorithm']) && $_GET['algorithm']=="lucene") echo 'checked="checked"';?>  value="lucene" /> Lucene(default)
			<input type="radio" name="algorithm" <?php if (isset($_GET['algorithm']) && $_GET['algorithm']=="pagerank") echo 'checked="checked"';?> value="pagerank" /> PageRank <br><br>
				    
			<div center>  
				<span class="fa fa-search" ><input type="submit" value="Submit"></input>
	    	        </div>
		      
		</form>
		
		<script>
			$(function() 
			{
		     		
			        var tags = [];

		     		$("#q").autocomplete(  							// Referred from https://api.jqueryui.com/autocomplete/
				{
					 source : function(request, response) 
					 {
						var before="";	
						var after="";
						var query_term = $("#q").val().toLowerCase();
						
						var whitespace =  query_term.lastIndexOf(' ');
					 	if(query_term.length-1>whitespace && whitespace!=-1) 
						{
							after=query_term.substr(whitespace+1);
							before = query_term.substr(0,whitespace);
						}
						else
						{
							after=query_term.substr(0); 
						}
			
				var URL = "http://localhost:8983/solr/myexample/suggest?q=" + after + "&wt=json&indent=true";
	
				$.ajax(
				{
					url : URL,

				  	success : function(data) 
					{
						
				  		var SERP =JSON.parse(JSON.stringify(data.suggest.suggest))[after].suggestions;
						var j=0;
						var tokens =[];

				  		for(var i=0;i<5 && j<SERP.length;i++,j++)
						{
							if(SERP[j].term==after)
							{
							      i--;
							      continue;
							}
						    
							for(var k=0;k<i && i>0;k++)
							{
								if(tags[k].indexOf(SERP[j].term) >=0)
								{
									i--;
									continue;
								}
						    	}
						    
							if(SERP[j].term.indexOf('.')>=0 || SERP[j].term.indexOf('_')>=0)
							{
								i--;
							        continue;
							}
							
							var s =(SERP[j].term);
						    
							if(tokens.length == 5)
								break;
						    
							if(tokens.indexOf(s) == -1)
						    	{
								tokens.push(s);
								if(before=="")
								{
									tags[i]=s;
							      	}
							        else
						      		{
									tags[i] = before+" ";
									tags[i]+=s;
							        }
						        }
						}
						
						response(tags);

						},

				dataType : 'jsonp',
				jsonp : 'json.wrf'

			      });
		      },
		     	 minLength : 1
		    })
		   });

		</script>

		<?php
			if ($flag)
			{
				echo $output;
			}

			$csv =  array_map('str_getcsv', file('URLtoHTML_latimes.csv'));
			
			if ($SERP)
			{
			  $total = (int) $SERP->response->numFound;
			  $start = min(1, $total);
			  $end = min($limit, $total);
		?>
		
		<div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
		<ol>

		<?php
		 
			foreach ($SERP->response->docs as $doc)
			{
			    
				$id = $doc->id;
			        $id = str_replace("/home/rahul/Desktop/HW4-Rahul/Data/latimes-20181112T035524Z-001/latimes/latimes/latimes/","",$id);
			        $title = $doc->title;

				if ( sizeof($title) >1)									
				{  
					$title = $title[0];
				}

			    	foreach ($csv as $key ) 
				{
			      		if ($id == $key[0])
					{
						$link = $key[1];
						break;
			      		}
			    	}
		    
				$end="\b)";
				$sentences = explode(".", file_get_contents("/home/rahul/Desktop/HW4-Rahul/Data/latimes-20181112T035524Z-001/latimes/latimes/latimes/" . $id));
				$words = explode(" ", $query);
				$snippet = "";
				$text = "/";
				$begin="(?=.*?\b";
				
				foreach($words as $item)
				{
					$text=$text.$begin.$item.$end;
				}

				$text=$text."^.*$/i";

				foreach($sentences as $sentence)
				{
					$sentence=strip_tags($sentence);
					if (preg_match($text, $sentence)>0)
					{
						if (preg_match("(&gt|&lt|\/|{|}|[|]|\|\%|>|<|:)",$sentence)>0)
						{
					  		continue;
						}
						else
						{
				  			$snippet = $snippet.$sentence;

				  			if(strlen($snippet)>160)    // Maximum allowed length of a snippet
							{
				   				break;
							}
						}
			       }
		    	}

			$words = preg_split('/\s+/', $query);
		  	
			foreach($words as $item)
				$snippet = str_ireplace($item, "<strong>".$item."</strong>",$snippet);
		    	
			if($snippet == "")
			{
		      		$snippet = "N/A";
		    	}
		  
		?>

		<li>
			<table>
			
				<tr>
					<qw><?php echo "<a target='_blank' href = '{$link}' STYLE='text-decoration:none'><font size='4px'><b>".$title."</b></font></a>" ?></qw>
			 	</tr>
				
				<tr>
					<th><?php echo "Link"; ?></th>
					<td><font color="green"><?php echo "<a  target='_blank' href = '{$link}' STYLE='text-decoration:none'><st><font color='green'>".$link."</font></st></a>" ?></font></td>
				</tr>
				
				<tr>
					<th><?php echo "ID"; ?></th>
					<td><?php echo htmlspecialchars($id, ENT_NOQUOTES, 'utf-8'); ?></td>
	 		        </tr>
				
				<tr>
				    	<th><?php echo "Snippet"; ?></th>
				 
				   	<td><?php 
				    
					if($snippet == "N/A")
					{
						echo htmlspecialchars($snippet, ENT_NOQUOTES, 'utf-8');
				    	}
					else
					{
						echo "...".$snippet."...";
				    	}
				    	?>
					</td>
				</tr>

				<tr>
				    <th><br></th>
				    <td><br></td>
				</tr>
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
