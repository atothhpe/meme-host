import {Component, ElementRef, ViewChild} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpEventType, HttpHeaders} from '@angular/common/http';
import {ModalService} from '../../services/modal.service';
import {environment} from '../../../environments/environment';
import {Subscription} from 'rxjs';
import {MemeService} from '../../services/meme.service';

@Component({
    selector: 'file-upload',
    templateUrl: './file-upload.component.html',
    styleUrls: ['./file-upload.component.less']
})
export class FileUploadComponent {

    @ViewChild('fileInput', {static: false})
    myInputVariable: ElementRef;

    uploadProgressPercentage = 0;

    uploadResultTitle: string;
    uploadResultDetails: string;

    subscription: Subscription;

    noFileSelected = 'No file selected...';
    uploadFileName: string = this.noFileSelected;

    constructor(private http: HttpClient, private modalService: ModalService, private memeService: MemeService) {
    }

    uploadFile(event) {
        this.uploadProgressPercentage = .1;

        const selectedFile: File = event.target.files[0];
        const fd = new FormData();
        fd.append('file', selectedFile, selectedFile.name);

        this.uploadFileName = 'Uploading "' + selectedFile.name + '"';

        const headers = new HttpHeaders();
        headers.append('Content-Type', 'multipart/form-data');
        headers.append('Accept', 'application/json');
        headers.append('enctype', 'multipart/form-data');

        this.subscription = this.http.post(environment.serverUrl + 'memes/upload', fd, {
            headers: headers,
            reportProgress: true,
            observe: 'events'
        }).subscribe(
            next => {
                if (next.type === HttpEventType.UploadProgress) {
                    this.uploadProgressPercentage = Math.round(next.loaded / next.total * 100);
                } else if (next.type === HttpEventType.Response) {
                    this.showModal('Upload complete', '');
                    this.resetUpload();
                    this.memeService.memeUploadedSubject.next(null);
                }
            },
            error => {

                let errorMessage;

                if (error instanceof HttpErrorResponse) {
                    const httpErrorResponse = <HttpErrorResponse>error;

                    if (httpErrorResponse.error['message']) {
                        errorMessage = httpErrorResponse.error['message'];
                    } else {
                        errorMessage = httpErrorResponse.message;
                    }
                } else {
                    errorMessage = 'Upload failed.';
                }

                this.showModal('Error', errorMessage);
                this.resetUpload();
            }
        );
    }

    cancelFileUpload() {
        this.subscription.unsubscribe();
        this.resetUpload();
    }

    showModal(title: string, details: string) {
        this.uploadResultTitle = title;
        this.uploadResultDetails = details;
        this.modalService.open('upload-result-modal');
    }

    resetUpload() {
        this.uploadFileName = this.noFileSelected;
        this.uploadProgressPercentage = 0;
        this.myInputVariable.nativeElement.value = '';
    }

    isUploadInProgress(): boolean {
        return this.uploadProgressPercentage !== 0;
    }

}
