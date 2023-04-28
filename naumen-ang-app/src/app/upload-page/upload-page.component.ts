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
  loading: boolean = false;
  file: File = null;

  constructor(private fileUploadService: FileUploadService) { }

  ngOnInit(): void {}

  onChange(event) {
    this.file = event.target.files[0];
  }

  onUpload() {
    if (this.file == null) {
      this.message = '';
      this.loading = false;
      this.error = 'Файл не выбран';
      return;
    }
    this.loading = true;
    this.error = '';
    this.message = '';
    this.fileUploadService.upload(this.file).subscribe(
      (response) => {
        this.message = response;
        this.error = null;
        this.loading = false;
      },
      (error) => {
        this.error = error.error;
        this.message = null;
        this.loading = false;
      }
    );
  }
}
