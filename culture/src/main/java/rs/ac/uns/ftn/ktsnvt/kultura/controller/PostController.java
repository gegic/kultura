package rs.ac.uns.ftn.ktsnvt.kultura.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.ktsnvt.kultura.model.Post;
import rs.ac.uns.ftn.ktsnvt.kultura.service.PostService;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/post/", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

    @Autowired
    private PostService postService;


    @GetMapping(path = "/{culturalOfferingId}/{pageNumber}/{pageSize}/{sort}/{desc}", produces = "application/json")
    public ResponseEntity<Page<Post>> get(@PathVariable String culturalOfferingId,
                                    @PathVariable int pageNumber,
                                    @PathVariable int pageSize,
                                    @PathVariable String sort,
                                    @PathVariable boolean desc){
        Pageable p;
        if (sort != null) {
            Sort s;
            if (desc) s = Sort.by(Sort.Direction.DESC, sort);
            else s = Sort.by(Sort.Direction.ASC, sort);
            p = PageRequest.of(--pageNumber, pageSize, s);
        } else p = PageRequest.of(--pageNumber, pageSize);
        return new ResponseEntity<>(this.postService.readAllByCulturalOfferingId(UUID.fromString(culturalOfferingId),
                p), HttpStatus.OK);
    }

    @PostMapping()
    ResponseEntity<Post> add(@RequestBody Post post){
        return new ResponseEntity<>(this.postService.save(post), HttpStatus.CREATED);
    }

    @PutMapping()
    ResponseEntity<Post> update(@RequestBody Post post){
        return new ResponseEntity<>(this.postService.save(post), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id){
        this.postService.delete(UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
