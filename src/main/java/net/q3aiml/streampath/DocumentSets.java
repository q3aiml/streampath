package net.q3aiml.streampath;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Set;

/**
 * @author q3aiml
 */
public class DocumentSets {

    private static final ImmutableDocumentSet EMPTY_DOCUMENT_SET
            = new ImmutableDocumentSet(null, ImmutableSet.<Document>of());

    public static DocumentSet empty() {
        return EMPTY_DOCUMENT_SET;
    }

    public static DocumentSetBuilder create(Source currentDocument) {
        return new DocumentSetBuilder(currentDocument);
    }

    public static DocumentSet ofSource(Source currentDocument) {
        return create(currentDocument).build();
    }

    private static class ImmutableDocumentSet implements DocumentSet {
        private final Document currentDocument;
        private final ImmutableSet<Document> allDocuments;

        public ImmutableDocumentSet(Document currentDocument, ImmutableSet<Document> allDocuments) {
            if (currentDocument != null && !allDocuments.contains(currentDocument)) {
                throw new IllegalArgumentException("allDocuments must contain currentDocument");
            }

            this.currentDocument = currentDocument;
            this.allDocuments = allDocuments;
        }

        public Document currentDocument() {
            return currentDocument;
        }

        public Set<Document> documents() {
            return allDocuments;
        }

        @Override
        public void close() throws IOException { }
    }

    private static class StreamDocument implements Document {
        private final String identifier;
        private final Source input;

        public StreamDocument(String identifier, Source input) {
            this.identifier = identifier;
            this.input = input;
        }

        @Nullable
        @Override
        public String identifier() {
            return identifier;
        }

        public XMLEventReader stream() throws IOException {
            try {
                return XMLInputFactory.newInstance().createXMLEventReader(input);
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
        }

        public void close() throws IOException {
            // don't need to close Source
        }
    }

    public static class DocumentSetBuilder {
        private final Document currentDocument;
        private final ImmutableSet.Builder<Document> documents = ImmutableSet.builder();

        private DocumentSetBuilder(Source currentDocumentSource) {
            currentDocument = new StreamDocument(null, currentDocumentSource);
            documents.add(currentDocument);
        }

        public DocumentSetBuilder add(String identifier, Source source) {
            documents.add(new StreamDocument(identifier, source));
            return this;
        }

        public DocumentSet build() {
            return new ImmutableDocumentSet(currentDocument, documents.build());
        }
    }
}
