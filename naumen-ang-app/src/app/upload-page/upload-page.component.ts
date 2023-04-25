import {Component, OnInit} from '@angular/core';
import {FileUploadService} from "../file-upload.service";

@Component({
  selector: 'app-upload-page',
  templateUrl: './upload-page.component.html',
  styleUrls: ['./upload-page.component.css']
})
export class UploadPageComponent implements OnInit {

  // Variable to store shortLink from api response
  text: string = null;
  loading: boolean = false; // Flag variable
  file: File = null; // Variable to store file

  // Inject service
  constructor(private fileUploadService: FileUploadService) { }

  ngOnInit(): void {
  }

  // On file Select
  onChange(event) {
    // @ts-ignore
    this.file = event.target.files[0];
  }

  // OnClick of button Upload
  onUpload() {
    this.loading = true;
    this.fileUploadService.upload(this.file).subscribe(
      (result) => {
        this.text = result;
        this.loading = false;
      }
    );
  }
}
