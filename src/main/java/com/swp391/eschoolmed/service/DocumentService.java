package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.UpdateDocumentRequest;
import com.swp391.eschoolmed.dto.response.DocumentResponse;
import com.swp391.eschoolmed.model.Document;
import com.swp391.eschoolmed.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    public List<Document> getAll() {
        return documentRepository.findAll();
    }

    public Document getById(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
    }

    public Document create(Document request) {
        Document doc = new Document();
        doc.setUserId(request.getUserId());
        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        return documentRepository.save(doc);
    }

    public void updateIfOwner(Long id, UpdateDocumentRequest request, Long userId) throws AccessDeniedException {
        Document doc = getById(id);
        if (!doc.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa bài viết này.");
        }
        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        documentRepository.save(doc);
    }


    public void deleteIfOwner(Long id, Long userId) throws AccessDeniedException {
        Document doc = getById(id);
        if (!doc.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền xóa bài viết này.");
        }
        documentRepository.delete(doc);
    }


    public DocumentResponse toResponse(Document doc) {
        return DocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .userId(doc.getUserId())
                .title(doc.getTitle())
                .content(doc.getContent())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}

