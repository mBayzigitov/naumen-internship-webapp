import {Component, OnInit} from '@angular/core';
import {FileUploadService} from "../file-upload.service";

@Component({
  selector: 'app-upload-page',
  templateUrl: './upload-page.component.html',
  styleUrls: ['./upload-page.component.css']
})
export class UploadPageComponent implements OnInit {

  message: string = null;
  error: string = null;
  file: File = null;

  constructor(private fileUploadService: FileUploadService) { }

  ngOnInit(): void {}

  onChange(event) {
    this.file = event.target.files[0];
  }

  onUpload() {
    this.message = 'Загрузка...';
    if (this.file == null) {
      this.message = '';
      this.error = 'Файл не выбран';
      return;
    }
    this.error = '';
    this.fileUploadService.upload(this.file).subscribe(
      (response) => {
        this.message = response;
        this.error = null;
      },
      (error) => {
        this.message = null;
        if (error.error instanceof ProgressEvent) {
          this.error = 'Выбранный файл был изменён. Загрузите его снова или выберите другой';
        } else {
          this.error = error.error;
        }
      }
    );
  }
}
