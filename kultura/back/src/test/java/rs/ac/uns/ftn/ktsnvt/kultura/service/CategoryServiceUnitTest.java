package rs.ac.uns.ftn.ktsnvt.kultura.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.ktsnvt.kultura.constants.CategoryConstants;
import rs.ac.uns.ftn.ktsnvt.kultura.dto.CategoryDto;
import rs.ac.uns.ftn.ktsnvt.kultura.mapper.Mapper;
import rs.ac.uns.ftn.ktsnvt.kultura.model.Category;
import rs.ac.uns.ftn.ktsnvt.kultura.repository.CategoryRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static rs.ac.uns.ftn.ktsnvt.kultura.constants.CategoryConstants.PAGE_SIZE;
import static rs.ac.uns.ftn.ktsnvt.kultura.constants.CategoryConstants.TEST_CATEGORY_ID1;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryServiceUnitTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private Mapper mapper;

    @Test(expected = NullPointerException.class)
    public void whenUpdateNullPointerException(){
        Mockito.when(categoryRepository.findById(null)).thenReturn(null);

        categoryService.update(null);
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenUpdateEntityNotFoundException(){
        CategoryDto category = new CategoryDto();
        category.setId(555L);

        Mockito.when(categoryRepository.findById(555L)).thenReturn(Optional.empty());

        categoryService.update(category);
    }


    @Test
    public void testReadAll() {
        ArrayList<Category> categories = new ArrayList<>();
        Category cat1 = new Category();
        Category cat2 = new Category();
        categories.add(cat1);
        categories.add(cat2);

        Pageable pageRequest = PageRequest.of(0, PAGE_SIZE);

        Mockito.when(categoryRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(categories));

        Page<CategoryDto> returnedCategories = categoryService.readAll(pageRequest);

        assertEquals(categories.size(), returnedCategories.getContent().size());

    }

    @Test(expected = EntityExistsException.class)
    public void whenCreateThrowEntityExists() {
//        Exception exception = assertThrows(EntityExistsException.class, () -> {
//            CategoryDto category = new CategoryDto();
//            category.setId(CategoryConstants.TEST_CATEGORY_ID1);
//            category.setName(CategoryConstants.TEST_CATEGORY_NAME1);
//
//            CategoryDto category2 = new CategoryDto();
//            category2.setId(CategoryConstants.TEST_CATEGORY_ID1);
//            category2.setName(CategoryConstants.TEST_CATEGORY_NAME1);
//
//            CategoryDto returnedCategory = categoryService.create(category);
//            CategoryDto returnedCategory2 = categoryService.create(category2);
//
//
//        });
//
//        String expectedMessage = "A category with this ID already exists";
//        String actualMessage = exception.getMessage();
//
//        assertTrue(actualMessage.contains(expectedMessage));

            CategoryDto category = new CategoryDto();
            category.setId(CategoryConstants.TEST_CATEGORY_ID1);
            category.setName(CategoryConstants.TEST_CATEGORY_NAME1);

            CategoryDto category2 = new CategoryDto();
            category2.setId(CategoryConstants.TEST_CATEGORY_ID1);
            category2.setName(CategoryConstants.TEST_CATEGORY_NAME1);

            Mockito.when(categoryRepository.existsById(CategoryConstants.TEST_CATEGORY_ID1)).thenReturn(true);

            categoryService.create(category);
            categoryService.create(category2);
    }


}