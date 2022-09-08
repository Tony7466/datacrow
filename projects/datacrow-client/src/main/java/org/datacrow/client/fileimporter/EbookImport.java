/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.org                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package org.datacrow.client.fileimporter;

import org.datacrow.core.fileimporter.FileImporter;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcObject;

/**
 * E-Book (Electronical Book) file imporerter.
 * 
 * @author Robert Jan van der Waals
 * 
 * TODO: re-evaluate & reimplement
 */
public class EbookImport extends FileImporter {

    //private static Logger logger = DcLogManager.getLogger(EbookImport.class.getName());
    
    public EbookImport() {
        super(DcModules._BOOK);
    }
    
    @Override
	public FileImporter getInstance() {
		return new EbookImport();
	}

	@Override
    public String[] getSupportedFileTypes() {
        return new String[] {
                "txt",  // N/A
                "chm",  // CHM parser
                "doc",  // Office Parse
                "pdf",  // PDF Box
                "epub", // EPUB Parser
                "prc",  
                "rtf",
                "pdb", 
                "kml", 
                "html",  // HTML Parser 
                "htm",   // HTML Parser
                "prc", 
                "lit", 
                "docx",  // Office Parser
                "odt"    // Open Office Parser
        }; 
    }
    
    @Override
    public boolean allowReparsing() {
        return true;
    }    
    
	@Override
    public DcObject parse(String filename, int directoryUsage) {
    /*    DcObject book = DcModules.get(DcModules._BOOK).getItem();
        
        try {
            book.setValue(Book._A_TITLE, getName(filename, directoryUsage));
            book.setValue(Book._SYS_FILENAME, filename);
            
            // check if the filename contains an ISBN
            String isbn = String.valueOf(StringUtils.getContainedNumber(filename));
            boolean isIsbn10 = ISBN.isISBN10(isbn);
            boolean isIsbn13 = ISBN.isISBN13(isbn);
            
            // this can be used later on by the online search
            if (isIsbn10 || isIsbn13) {
                String isbn10 = isIsbn10 ? isbn : ISBN.getISBN10(isbn);
                String isbn13 = isIsbn13 ? isbn : ISBN.getISBN13(isbn);
                book.setValue(Book._J_ISBN10, isbn10);
                book.setValue(Book._N_ISBN13, isbn13);
            }
            
            if (!filename.toLowerCase().endsWith("pdf")) {
                // non PDF files are handled with the Tika library
                InputStream is = null;
                try {
                    is = new FileInputStream(new File(filename));
                    ContentHandler textHandler = new BodyContentHandler();
                    Metadata metadata = new Metadata();
                    ParseContext context = new ParseContext();
                    Parser parser = null;
                    if (filename.toLowerCase().endsWith("chm")) {
                        parser = new org.apache.tika.parser.chm.ChmParser();
                    } else if (filename.toLowerCase().endsWith("doc") || filename.toLowerCase().endsWith("docx")) {
                        parser = new org.apache.tika.parser.microsoft.OfficeParser();
                    } else if (filename.toLowerCase().endsWith("htm") || filename.toLowerCase().endsWith("html")) {
                        parser = new org.apache.tika.parser.html.HtmlParser();
                    } else if (filename.toLowerCase().endsWith("odt")) {
                        parser = new org.apache.tika.parser.odf.OpenDocumentMetaParser();
                    } else if (filename.toLowerCase().endsWith("epub")) {
                        parser = new org.apache.tika.parser.epub.EpubParser();
                    }
                    
                    if (parser != null) {
                        parser.parse(is, textHandler, metadata, context);
                        
                        String author = metadata.get(Metadata.AUTHOR);
                        String creator = metadata.get(Metadata.CREATOR);
                        
                        String description = metadata.get(Metadata.DESCRIPTION);
                        String publisher = metadata.get(Metadata.PUBLISHER);
                        String pagecount = metadata.get(Metadata.PAGE_COUNT);
                        String title = metadata.get(Metadata.TITLE);
                        
                        if (!CoreUtilities.isEmpty(author))
                        	book.createReference(Book._G_AUTHOR, author);
                        else if (!CoreUtilities.isEmpty(creator))
                        	book.createReference(Book._G_AUTHOR, creator);
    
                        if (!CoreUtilities.isEmpty(title))
                            book.setValue(Book._A_TITLE, title);
    
                        if (!CoreUtilities.isEmpty(description))
                            book.setValue(Book._B_DESCRIPTION, description);
    
                        if (!CoreUtilities.isEmpty(publisher))
                        	book.createReference(Book._F_PUBLISHER, publisher);
                        
                        if (!CoreUtilities.isEmpty(pagecount)) {
                            try { 
                                book.setValue(Book._T_NROFPAGES, Long.parseLong(pagecount));
                            } catch (NumberFormatException nfe) {
                                logger.debug("Could not parse number of pages for " + pagecount, nfe);
                            }
                        }
                    }
                } finally {
                    if (is != null) is.close();
                }
            } else if (filename.toLowerCase().endsWith("pdf")) {
                // PDF files are handled the old fashioned way with PDFBox
                File file = new File(filename);
                FileISBNExtractor fileISBNExtractor = new FileISBNExtractor();
                fileISBNExtractor.setSearchMinBytes(30000);
                fileISBNExtractor.getTextReaderFactory().setPreferredPdfExtractor(new PDFBoxTextExtractor());
                ISBNCandidates isbnCandidates = fileISBNExtractor.getIsbnCandidates(file);
                org.chabanois.isbn.extractor.ISBN extractedISBN = isbnCandidates.getHighestScoreISBN();
                
                if (extractedISBN != null ) {
                    String s = extractedISBN.getIsbn();
                    if (s != null && s.length() > 0)
                        book.setValue(Book._N_ISBN13, ISBN.isISBN10(s) ? ISBN.getISBN13(s) : 
                                                      ISBN.isISBN13(s) ? s : null);
                }
                
                RandomAccessFile raf = null;
                PDFFile pdffile;
                try {
                    
                    raf = new RandomAccessFile(file, "r");
                    FileChannel channel = raf.getChannel();
                    ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                    channel.close();
                    
                    pdffile = new PDFFile(buf);
                    pdffile.stop(1);
    
                    try {
                        book.setValue(Book._T_NROFPAGES, Long.valueOf(pdffile.getNumPages()));
                        Iterator<String> it = pdffile.getMetadataKeys();
                        while (it.hasNext()) {
                            String key = it.next();
                            String value = pdffile.getStringMetadata(key);
                            
                            if (!CoreUtilities.isEmpty(value)) {
                                if (key.equalsIgnoreCase("Author"))
                                	book.createReference(Book._G_AUTHOR, value);
                                if (key.equalsIgnoreCase("Title") && !value.trim().equalsIgnoreCase("untitled"))
                                    book.setValue(Book._A_TITLE, value);
                            }
                        }
                    } catch (IOException ioe) {
                        getClient().notify(DcResources.getText("msgCouldNotReadInfoFrom", filename));
                    }
    
                    // draw the first page to an image
                    PDFPage page = pdffile.getPage(0);
                    if (page != null) {
                        Rectangle rect = new Rectangle(0,0, (int)page.getBBox().getWidth(), (int)page.getBBox().getHeight());
                        Image front = page.getImage(rect.width, rect.height, rect, null, true, true);
                        book.setValue(Book._K_PICTUREFRONT, new DcImageIcon(CoreUtilities.getBytes(new DcImageIcon(front))));
                    }
                } finally {
                    if (raf != null) raf.close();
                }
            }
            
            Hash.getInstance().calculateHash(book);
        } catch (OutOfMemoryError err) {
            logger.error(err, err);
            getClient().notify(DcResources.getText("msgOutOfMemory"));
        } catch (Exception exp) {
            logger.error(exp, exp);
            getClient().notify(DcResources.getText("msgCouldNotReadInfoFrom", filename));
        } catch (Error err) {
            logger.error(err, err);
            getClient().notify(DcResources.getText("msgCouldNotReadInfoFrom", filename));
        }
        return book; */
    	
    	return null;
    }
}
