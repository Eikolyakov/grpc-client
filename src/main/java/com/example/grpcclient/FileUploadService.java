package com.example.grpcclient;

import com.google.protobuf.ByteString;
import io.grpc.netty.shaded.io.netty.handler.codec.http.multipart.FileUpload;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.vsk.s3servicedms.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

@Service
public class FileUploadService {

        @GrpcClient("fileServiceStub")
        private FileServiceGrpc.FileServiceStub fileServiceStub;

        public void uploadFile() throws IOException {
            StreamObserver<FileUploadRequest> streamObserver = fileServiceStub.upload(new FileUploadObserver());
            // input file for testing
            Path path = Paths.get("src/main/resources/input/java_inpt.pdf");

            // build metadata
            FileUploadRequest metadata = FileUploadRequest.newBuilder()
                    .setMetadata(MetaData.newBuilder()
                            .setName("output")
                            .setType("pdf").build())
                    .build();
            streamObserver.onNext(metadata);

            // upload bytes
            InputStream inputStream = Files.newInputStream(path);
            byte[] bytes = new byte[4096];
            int size;
            while ((size = inputStream.read(bytes)) > 0){
                FileUploadRequest uploadRequest = FileUploadRequest.newBuilder()
                        .setFile(File.newBuilder().setContent(ByteString.copyFrom(bytes, 0 , size)).build())
                        .build();
                streamObserver.onNext(uploadRequest);
            }
            // close the stream
            inputStream.close();
            streamObserver.onCompleted();

        }

    private static class FileUploadObserver implements StreamObserver<FileUploadResponse> {


        @Override
        public void onNext(FileUploadResponse fileUploadResponse) {
            System.out.println(
                    "File upload status :: " + fileUploadResponse.getStatus()
            );
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onCompleted() {
            System.out.println("Done");
        }
    }
}
