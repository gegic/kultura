import {ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import {ActivatedRoute, Router} from '@angular/router';
import {ModeratorService} from '../../core/services/moderator/moderator.service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {DialogService} from 'primeng/dynamicdialog';
import {Moderator} from '../../core/models/moderator';
import {Observable, of, throwError} from 'rxjs';
import {DialogModule} from 'primeng/dialog';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CardModule} from 'primeng/card';
import {ModeratorEditComponent} from './moderator-edit.component';
import {RegisterService} from '../../core/services/register/register.service';

describe('ModeratorAddComponent', () => {
  let component: ModeratorEditComponent;
  let fixture: ComponentFixture<ModeratorEditComponent>;
  let router: Router;
  let moderatorService: ModeratorService;
  let messageService: MessageService;
  let dialogService: DialogService;
  let activatedRoute: ActivatedRoute;
  let registerService: RegisterService;

  beforeEach(async () => {
    const moderator: Moderator = {
      id: '1',
      email: 'test1@mail.com',
      firstName: 'Firstname1',
      lastName: 'Lastname1'
    };

    const moderatorForUpdater: Moderator = {
      id: '1',
      email: 'test1@mail.com',
      firstName: 'Firstname1',
      lastName: 'Lastname1'
    };

    const activatedRouteMock = {queryParams: of({lat: 10, lng: 11})};

    const registerServiceMock = {
      getIdByMail: jasmine.createSpy('getIdByMail').and.returnValue(of(moderator))
    };
    const moderatorServiceMocked = {
      getModeratorById: jasmine.createSpy('getModeratorById').and.returnValue(of(moderator)),
      updateModerator: jasmine.createSpy('updateModerator').and.returnValue(of(moderatorForUpdater)),
    };

    const messageServiceMocked = jasmine.createSpyObj(
      'MessageService',
      ['add'],
      ['messages']
    );
    const dialogServiceMocked = {
      open: jasmine.createSpy('open').and.returnValue(of({}))
    };

    const routerMock = {
      navigate: jasmine.createSpy('navigate')
    };


    await TestBed.configureTestingModule({
      declarations: [ModeratorEditComponent],
      providers:    [
        {provide: ModeratorService, useValue: moderatorServiceMocked },
        { provide: MessageService, useValue: messageServiceMocked },
        { provide: DialogService, useValue: dialogServiceMocked},
        {provide: ConfirmationService, useValue: ConfirmationService},
        {provide: RegisterService, useValue: registerServiceMock},
        {provide: Router, useValue: routerMock},
        {provide: ActivatedRoute, useValue: activatedRouteMock}],
      imports: [DialogModule, ReactiveFormsModule, FormsModule, CardModule]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    registerService = TestBed.inject(RegisterService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be edit moderator but invalid form when submitted and last name is empty', () => {
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    component.ngOnInit();

    component.moderatorForm.controls.firstName.setValue('Miar');
    component.moderatorForm.controls.lastName.setValue('');
    component.moderatorForm.controls.email.setValue('test@mail.com');
    component.moderatorForm.controls.password.setValue('miratMiric123');
    component.moderatorForm.controls.repeatPassword.setValue('miratMiric123');
    component.onSubmit();

    expect(component.moderatorForm.invalid).toBeTruthy();
    expect(moderatorService.createModerator).toHaveBeenCalledTimes(0);
  });

  it('should be initialized', () => {
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    component.ngOnInit();

    expect(component.moderatorForm).toBeDefined();
    expect(component.moderatorForm.invalid).toBeTruthy();
  });

  it('should be edit moderator but invalid form when submitted and first name is empty', () => {
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    component.ngOnInit();

    component.moderatorForm.controls.firstName.setValue('');
    component.moderatorForm.controls.lastName.setValue('Miric');
    component.moderatorForm.controls.email.setValue('test@mail.com');
    component.moderatorForm.controls.password.setValue('miratMiric123');
    component.moderatorForm.controls.repeatPassword.setValue('miratMiric123');
    component.onSubmit();

    expect(component.moderatorForm.invalid).toBeTruthy();
    expect(moderatorService.createModerator).toHaveBeenCalledTimes(0);

    /*const errorMsg = fixture.debugElement.query(By.css('#toast-container'));
    expect(errorMsg).toBeDefined();*/
  });

  it('should be edit moderator successfully when submitted', fakeAsync(() => {
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    component.ngOnInit();

    component.moderatorForm.controls.firstName.setValue('Mitar');
    component.moderatorForm.controls.lastName.setValue('Miric');
    component.moderatorForm.controls.email.setValue('test@mail.com');
    component.moderatorForm.controls.password.setValue('miratMiric123');
    component.moderatorForm.controls.repeatPassword.setValue('miratMiric123');
    component.onSubmit();

    expect(component.moderatorForm.invalid).toBeFalse();
    expect(moderatorService.createModerator).toHaveBeenCalledTimes(1);
  }));

  it('should be edit moderator but invalid form when submitted and passwords incorrect', fakeAsync(() => {
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    component.ngOnInit();

    component.moderatorForm.controls.firstName.setValue('Mitar');
    component.moderatorForm.controls.lastName.setValue('Miric');
    component.moderatorForm.controls.email.setValue('test@mail.com');
    component.moderatorForm.controls.password.setValue('');
    component.moderatorForm.controls.repeatPassword.setValue('miratMiric123');
    component.onSubmit();

    expect(component.moderatorForm.invalid).toBeTruthy();
    expect(moderatorService.createModerator).toHaveBeenCalledTimes(0);
  }));

  it('should be edit moderator but invalid form when submitted and mail incorrect', fakeAsync(() => {
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    component.ngOnInit();

    component.moderatorForm.controls.firstName.setValue('Mitar');
    component.moderatorForm.controls.lastName.setValue('Miric');
    component.moderatorForm.controls.email.setValue('test');
    component.moderatorForm.controls.password.setValue('');
    component.moderatorForm.controls.repeatPassword.setValue('miratMiric123');
    component.onSubmit();

    expect(component.moderatorForm.invalid).toBeTruthy();
    expect(moderatorService.createModerator).toHaveBeenCalledTimes(0);
  }));

  it('should be edit moderator but invalid form when submitted and mail exist and wrong password', fakeAsync(() => {
    const moderatorServiceMailExists = {
      add: jasmine.createSpy('add').and.returnValue(throwError({
        status: 409,
        error: 'Email already exists.',
        message: 'Email already exists.',
      }))
    };
    fixture = TestBed.createComponent(ModeratorEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    moderatorService = TestBed.inject(ModeratorService);
    messageService =  TestBed.inject(MessageService);
    dialogService =  TestBed.inject(DialogService);
    component.ngOnInit();

    component.moderatorForm.controls.firstName.setValue('Mitar');
    component.moderatorForm.controls.lastName.setValue('Miric');
    component.moderatorForm.controls.email.setValue('test');
    component.moderatorForm.controls.password.setValue('');
    component.moderatorForm.controls.repeatPassword.setValue('miratMiric123');
    component.onSubmit();

    expect(component.moderatorForm.invalid).toBeTruthy();
    expect(moderatorService.createModerator).toHaveBeenCalledTimes(0);
    expect(messageService.add).toHaveBeenCalledTimes(2);
  }));


});
