import { Component, OnInit } from '@angular/core';
import {LoginService} from '../../../../core/services/login/login.service';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {ActivatedRoute, Route, Router} from '@angular/router';
import {MessageService} from 'primeng/api';
import {User} from '../../../../core/models/user';

@Component({
  selector: 'app-enter-email',
  templateUrl: './enter-email.component.html',
  styleUrls: ['./enter-email.component.scss']
})
export class EnterEmailComponent implements OnInit {

  emailControl: FormControl;

  constructor(private loginService: LoginService,
              private router: Router,
              private messageService: MessageService,
              private activatedRoute: ActivatedRoute) {
    this.emailControl = new FormControl();
  }

  ngOnInit(): void {
  }

  onClickProceed(): void {
    this.loginService.checkExistence(this.emailControl.value)
      .subscribe(
        (data: {value: string}) => {
          this.loginService.email = this.emailControl.value;
          this.loginService.name = data.value;
          this.router.navigate(['./password'], {relativeTo: this.activatedRoute});
        },
        err => {
          this.emailControl.reset();
          this.messageService.add({severity: 'error', summary: 'Email not found', detail: 'A user with this email doesn\'t exist.'});
        }
      );
  }

  onClickNewAccount(): void {
    this.loginService.reset();
    this.router.navigateByUrl('/register');
  }

}