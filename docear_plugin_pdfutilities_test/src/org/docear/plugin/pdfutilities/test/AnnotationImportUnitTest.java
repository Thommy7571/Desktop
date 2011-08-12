package org.docear.plugin.pdfutilities.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.docear.plugin.pdfutilities.features.PdfAnnotationExtensionModel;
import org.docear.plugin.pdfutilities.features.PdfAnnotationExtensionModel.AnnotationType;
import org.docear.plugin.pdfutilities.pdf.PdfAnnotationImporter;
import org.docear.plugin.pdfutilities.pdf.PdfFileFilter;
import org.junit.Assert;
import org.junit.Test;

import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.parser.COSLoadException;

public class AnnotationImportUnitTest {

	@Test
	public void testAnnotationImport() {
		File pdfDir = new File("C:\\_Dissertation\\Literature");
		
		List<File> pdfFiles = new ArrayList<File>();
		pdfFiles.addAll(Arrays.asList(pdfDir.listFiles(new PdfFileFilter())));
		int failCounter = 0;
		int ioExceptionCounter = 0;
		int cosLoadExceptionCounter = 0;
		int classCastExceptionCounter = 0;
		int cosRuntimeExceptionCounter = 0;
		int pageNotFoundCounter = 0;
		int objectNumberNotFoundCounter = 0;
		int objectNumberNotUniqueCounter = 0;
		int generationNumberNotFoundCounter = 0;
		int totalFiles = pdfFiles.size();		
		
		
		for(File pdfFile : pdfFiles){
			try {
				System.out.println("Testing file " + (pdfFiles.indexOf(pdfFile) + 1) + " of " + totalFiles);			
				PdfAnnotationImporter importer = new PdfAnnotationImporter();
				importer.setImportAll(true);
				List<PdfAnnotationExtensionModel> annotations = importer.importAnnotations(pdfFile);
				importer.setImportAll(false);
				boolean fileFailed = false;
				if(checkPages(annotations) == false){
					fileFailed = true;
					pageNotFoundCounter++;
					//copy(pdfFile, pageNotFoundFiles);
				}
				if(checkObjectNumber(annotations) == false){
					fileFailed = true;
					objectNumberNotFoundCounter++;
				}
				if(checkObjectNumberUnique(annotations, new ArrayList<Integer>()) == false){
					fileFailed = true;
					objectNumberNotUniqueCounter++;
				}
				if(checkGenerationNumber(annotations) == false){
					fileFailed = true;
					generationNumberNotFoundCounter++;
				}
				if(fileFailed){
					failCounter++;
				}
			} catch (IOException e) {
				System.out.println("IOException on file: " + pdfFile.getAbsolutePath());
				failCounter++;
				ioExceptionCounter++;
				//copy(pdfFile, ioExceptionFiles);
			} catch (COSLoadException e) {
				System.out.println("COSLoadException on file: " + pdfFile.getAbsolutePath());
				failCounter++;
				cosLoadExceptionCounter++;
				//copy(pdfFile, cosLoadExceptionFiles);
			} catch(COSRuntimeException e) {
				System.out.println("COSRuntimeException on file: " + pdfFile.getAbsolutePath());
				failCounter++;
				cosRuntimeExceptionCounter++;
				//copy(pdfFile, cosRuntimeExceptionFiles);
			} catch(ClassCastException e){
				System.out.println("ClassCastException on file: " + pdfFile.getAbsolutePath());
				failCounter++;
				classCastExceptionCounter++;
				//copy(pdfFile, classCastExceptionFiles);
			}
		}
		System.out.println("============================================");
		System.out.println("IOException total: " + ioExceptionCounter);
		System.out.println("COSLoadException total: " + cosLoadExceptionCounter);
		System.out.println("COSRuntimeException total: " + cosRuntimeExceptionCounter);
		System.out.println("ClassCastException total: " + classCastExceptionCounter);
		System.out.println("Page not found total: " + pageNotFoundCounter);
		System.out.println("Object number not found total: " + objectNumberNotFoundCounter);
		System.out.println("Object number not unique total: " + objectNumberNotUniqueCounter);
		System.out.println("Generation number not found total: " + generationNumberNotFoundCounter);
		System.out.println("Tested files total :" + totalFiles);
		System.out.println("Failed files count total :" + failCounter);
		Assert.assertEquals(0, failCounter);
	}

	@Test
	public void testAnnotationImportWithOneFile() {
		File pdfFile = new File("C:\\_Dissertation\\Literature\\Document expansion for speech retrieval.PDF");
		//Working file for reference
		//File pdfFile = new File("C:\\_Dissertation\\Literature\\07_Chim_A New Suffix Tree Similarity Measure for Document Clustering.PDF");
		int failCounter = 0;
		int ioExceptionCounter = 0;
		int cosLoadExceptionCounter = 0;
		int classCastExceptionCounter = 0;
		int cosRuntimeExceptionCounter = 0;
		int pageNotFoundCounter = 0;
		
		try {
			System.out.println("Testing file " + pdfFile.getAbsolutePath());			
			
			List<PdfAnnotationExtensionModel> annotations = new PdfAnnotationImporter().importAnnotations(pdfFile);
			if(checkPages(annotations) == false){
				failCounter++;
				pageNotFoundCounter++;				
			}
		} catch (IOException e) {
			System.out.println("IOException on file: " + pdfFile.getAbsolutePath());
			failCounter++;
			ioExceptionCounter++;			
		} catch (COSLoadException e) {
			System.out.println("COSLoadException on file: " + pdfFile.getAbsolutePath());
			failCounter++;
			cosLoadExceptionCounter++;			
		} catch(COSRuntimeException e) {
			System.out.println("COSRuntimeException on file: " + pdfFile.getAbsolutePath());
			failCounter++;
			cosRuntimeExceptionCounter++;			
		} catch(ClassCastException e){
			System.out.println("ClassCastException on file: " + pdfFile.getAbsolutePath());
			failCounter++;
			classCastExceptionCounter++;			
		}
		
		System.out.println("============================================");
		System.out.println("IOException total: " + ioExceptionCounter);
		System.out.println("COSLoadException total: " + cosLoadExceptionCounter);
		System.out.println("COSRuntimeException total: " + cosRuntimeExceptionCounter);
		System.out.println("ClassCastException total: " + classCastExceptionCounter);
		System.out.println("Page not found total: " + pageNotFoundCounter);
		System.out.println("Tested files total :" + 1);
		System.out.println("Failed files count total :" + failCounter);
		Assert.assertEquals(0, failCounter);		
	}
	
	
	private boolean checkGenerationNumber(List<PdfAnnotationExtensionModel> annotations) {
		boolean result = true;
		for(PdfAnnotationExtensionModel annotation : annotations){
			if(annotation.getGenerationNumber() == null || annotation.getGenerationNumber() < 0){
				System.out.println("Could not get Generation Number from annotation: " + annotation.getTitle());
				System.out.println("Annotation file: " + annotation.getFile().getAbsolutePath());
				System.out.println("Annotation type: " + annotation.getAnnotationType());
				System.out.println("Annotation Objectnumber: " + annotation.getObjectNumber());
				System.out.println("Annotation Generationnumber: " + annotation.getGenerationNumber());
				result =  false;
			}
			if(checkGenerationNumber(annotation.getChildren()) == false){
				result = false;
			}
		}
		return result;
	}

	private boolean checkObjectNumberUnique(List<PdfAnnotationExtensionModel> annotations, List<Integer> objectNumbers) {
		boolean result = true;
		for(PdfAnnotationExtensionModel annotation : annotations){
			if(annotation.getObjectNumber() != null && annotation.getObjectNumber() > 0){
				if(objectNumbers.contains(annotation.getObjectNumber())){
					System.out.println("Could not get Generation Number from annotation: " + annotation.getTitle());
					System.out.println("Annotation file: " + annotation.getFile().getAbsolutePath());
					System.out.println("Annotation type: " + annotation.getAnnotationType());
					System.out.println("Annotation Objectnumber: " + annotation.getObjectNumber());
					System.out.println("Annotation Generationnumber: " + annotation.getGenerationNumber());
					result =  false;
				}
				else{
					objectNumbers.add(annotation.getObjectNumber());
				}
			}
			
			if(checkObjectNumberUnique(annotation.getChildren(), objectNumbers) == false){
				result = false;
			}
		}
		return result;
	}

	private boolean checkObjectNumber(List<PdfAnnotationExtensionModel> annotations) {
		boolean result = true;
		for(PdfAnnotationExtensionModel annotation : annotations){
			if(annotation.getObjectNumber() == null || annotation.getObjectNumber() <= 0){
				System.out.println("Could not get Object Number from annotation: " + annotation.getTitle());
				System.out.println("Annotation file: " + annotation.getFile().getAbsolutePath());
				System.out.println("Annotation type: " + annotation.getAnnotationType());
				System.out.println("Annotation Objectnumber: " + annotation.getObjectNumber());
				System.out.println("Annotation Generationnumber: " + annotation.getGenerationNumber());
				result =  false;
			}
			if(checkObjectNumber(annotation.getChildren()) == false){
				result = false;
			}
		}
		return result;
	}

	private boolean checkPages(List<PdfAnnotationExtensionModel> annotations) {
		boolean result = true;
		for(PdfAnnotationExtensionModel annotation : annotations){
			if(annotation.getPage() == null && annotation.getAnnotationType() != AnnotationType.BOOKMARK_WITH_URI && annotation.getAnnotationType() != AnnotationType.BOOKMARK_WITHOUT_DESTINATION){
				System.out.println("Could not get page from annotation: " + annotation.getTitle());
				System.out.println("Annotation file: " + annotation.getFile().getAbsolutePath());
				System.out.println("Annotation type: " + annotation.getAnnotationType());
				result =  false;
			}
			if(checkPages(annotation.getChildren()) == false){
				result = false;
			}
		}
		return result;
	}
	
	

}