<?php
include('config.php');
require('vendor/fpdf.php');
$day=$_POST['day'];
unlink("pdf/doc.pdf");
    $pdf = new FPDF(); 
    $pdf->AddPage('l');
    $sql="select * from user";
    $width_cell=array(25,25,20,20,20,20,40,40,30,30);
    $pdf->SetFont('Arial','B',8);
    //Background color of header//
    $pdf->SetFillColor(193,229,252);
    
    // Header starts /// 
    //First header column //
    $pdf->Cell($width_cell[0],10,'Calling No.',1,0,'C',true);
    //Second header column//
    $pdf->Cell($width_cell[1],10,'Called No.',1,0,'C',true);
    //Third header column//
    $pdf->Cell($width_cell[2],10,'Date',1,0,'C',true); 
    //Fourth header column//
    $pdf->Cell($width_cell[3],10,'Time',1,0,'C',true);
    //Third header column//
    $pdf->Cell($width_cell[4],10,'Duration',1,0,'C',true); 
    
    $pdf->Cell($width_cell[5],10,'Call Type',1,0,'C',true); 
    $pdf->Cell($width_cell[6],10,'IMEI',1,0,'C',true); 
    $pdf->Cell($width_cell[7],10,'IMSI',1,0,'C',true); 
    $pdf->Cell($width_cell[8],10,'FIRST CELL ID',1,0,'C',true); 
    $pdf->Cell($width_cell[9],10,'SECOND CELL',1,1,'C',true); 
    //// header ends ///////
    
    $pdf->SetFont('Arial','',10);
    //Background color of header//
    $pdf->SetFillColor(235,236,236); 
    //to give alternate background fill color to rows// 
    $fill=false;
    foreach ($connect->query($sql) as $row) {
         $daynum=date("w", strtotime($row['Date']));
         if($daynum==$day)
         {
            $pdf->Cell($width_cell[0],10,$row['Calling_No'],1,0,'C',$fill);
            $pdf->Cell($width_cell[1],10,$row['Called_No'],1,0,'C',$fill);
            $pdf->Cell($width_cell[2],10,$row['Date'],1,0,'C',$fill);
            $pdf->Cell($width_cell[3],10,$row['Time'],1,0,'C',$fill);
            $pdf->Cell($width_cell[4],10,$row['Duration'],1,0,'C',$fill);
            $pdf->Cell($width_cell[5],10,$row['Call_Type'],1,0,'C',$fill);
            $pdf->Cell($width_cell[6],10,$row['IMEI'],1,0,'C',$fill);
            $pdf->Cell($width_cell[7],10,$row['IMSI'],1,0,'C',$fill);
            $pdf->Cell($width_cell[8],10,$row['First_Cell_ID'],1,0,'C',$fill);
            $pdf->Cell($width_cell[9],10,$row['Second_Cell'],1,1,'C',$fill);
         }
    }
    /// end of records /// 
    $pdf->Output('pdf/doc.pdf','F');
   if(file_exists("pdf/doc.pdf")){
       echo "200";
     
   }else{
       echo "404";
   }
?>