import {Component, ElementRef} from '@angular/core';
import {HttpClient, HttpEventType, HttpHeaders} from "@angular/common/http";
import {ViewChild} from '@angular/core';
import {ModalService} from "../../services/ModalService";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {

    @ViewChild('fileInput')
    myInputVariable: ElementRef;
    uploadProgressPercentage: number = 0;
    uploadResult: string;

    constructor(private http: HttpClient, private modalService: ModalService) {
    }

    onUpload(event) {
        this.uploadProgressPercentage = 1;

        let selectedFile: File = event.target.files[0]
        const fd = new FormData();

        fd.append('file', selectedFile, selectedFile.name);

        let headers = new HttpHeaders();
        headers.append('Content-Type', 'multipart/form-data');
        headers.append('Accept', 'application/json');
        headers.append('enctype', 'multipart/form-data');

        this.http.post('http://localhost:8090/memes/upload', fd, {
            headers: headers,
            reportProgress: true,
            observe: 'events'
        }).subscribe(
            event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.uploadProgressPercentage = Math.round(event.loaded / event.total * 100);
                } else if (event.type === HttpEventType.Response) {
                    this.showModal("Upload complete", event.statusText);
                    this.resetUpload();
                }
            },
            error => {
                this.showModal("Error", error.message);
                this.resetUpload();
            }
        );
    }

    showModal(title: string, message: string) {
        this.uploadResult = '<h1>' + title + '</h1>' + message;
        this.modalService.open("upload-result-modal");
    }

    resetUpload() {
        this.uploadProgressPercentage = 0;
        this.myInputVariable.nativeElement.value = "";
    }

    isUploadInProgress(): boolean {
        return this.uploadProgressPercentage !== 0;
    }

}